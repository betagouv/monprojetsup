import os
from collections import defaultdict
from typing import Annotated, Any, Dict, Iterator, List, Literal
from urllib.parse import quote_plus

import psycopg as pg
from dotenv import load_dotenv
from psycopg.rows import DictRow, class_row, dict_row
from psycopg.sql import SQL, Identifier
from pydantic import BaseModel, BeforeValidator, Field

from app.config import LOGGER
from app.db_schema import DbColumns, DbTables, JsonNestedFields
from app.domain.models.config import ProfileConfig
from app.domain.models.profile import Item, Profile
from app.domain.ports.data_repository import DataRepository


class PostgresDatabase(DataRepository):
    def __init__(
        self, hostname: str, port: str, db_name: str, username: str, password: str
    ) -> None:
        super().__init__()
        encoded_username = quote_plus(username)
        encoded_pw = quote_plus(password)
        encoded_dbname = quote_plus(db_name)
        LOGGER.info(
            f"Connecting to databse '{db_name}' at '{hostname}:{port}' with username '{username}'."
        )

        connection_uri = f"postgresql://{hostname}:{port}?user={encoded_username}&password={encoded_pw}&dbname={encoded_dbname}"
        self.connection: pg.Connection[DictRow] = pg.connect(
            connection_uri,
            row_factory=dict_row,  # type: ignore
        )

    @staticmethod
    def from_env() -> "PostgresDatabase":
        load_dotenv()

        DB_HOSTNAME: str = os.getenv("DB_SUGGESTIONS2_HOSTNAME", default="localhost")
        DB_PORT: str = get_env_or_raise("DB_SUGGESTIONS2_PORT")
        DB_USERNAME: str = get_env_or_raise("DB_SUGGESTIONS2_USERNAME")
        DB_PASSWORD: str = get_env_or_raise("DB_SUGGESTIONS2_PASSWORD")
        DB_NAME: str = get_env_or_raise("DB_SUGGESTIONS2_NAME")
        return PostgresDatabase(
            hostname=DB_HOSTNAME,
            port=DB_PORT,
            db_name=DB_NAME,
            username=DB_USERNAME,
            password=DB_PASSWORD,
        )

    def iter_profiles_batched(
        self,
        table_name: str,
        config: ProfileConfig,
        batch_size: int = 10000,
    ) -> Iterator[list[Profile]]:
        """
        Iterator that loads profiles from the given table in batches.
        """
        LOGGER.info(f"Loading profiles from '{table_name}' (batched)...")
        query = SQL("SELECT * FROM {}").format(Identifier(table_name))

        # NOTE: 'name=server_cursor_profiles' is needed to create a server-side cursor (by default it is client-side)
        with self.connection.cursor(
            name="server_cursor_profiles", row_factory=class_row(StudentDbRow)
        ) as cursor:
            cursor.execute(query)
            total = 0

            while True:
                students_data = cursor.fetchmany(batch_size)
                if not students_data:
                    break

                profiles = [s.to_profile(config) for s in students_data]
                total += len(profiles)
                yield profiles

        LOGGER.info(f"Loaded {total} profiles from '{table_name}'.")

    def iter_paniers_voeux_as_profiles_batched(
        self,
        paniers_table: str = DbTables.PANIERS_VOEUX,
        join_table: str = DbTables.JOIN_FORMATION_VOEU,
        batch_size: int = 10000,
    ) -> Iterator[list[Profile]]:
        """
        Iterator that load, in batches, wish baskets "parcoursup" and converts them to Profiles for NaiveBayesMatrix.

        paniers_table contains list of taXXX wishes that map to list of flXXX formations via join_table.

        Each basket becomes a Profile where:
        - features = the formations of the basket, category "formations_favorites"
        - targets = the same formations

        Returns:
            List of Profiles representing formation co-occurrences
        """
        voeu_to_formations = self._load_voeu_to_formations_mapping(join_table)

        LOGGER.info(f"Loading paniers de voeux from '{paniers_table}' (batched)...")
        # TODO: remove this LIMIT after integration test.
        query_paniers = SQL("SELECT id, bac, voeux FROM {} LIMIT 15000").format(
            Identifier(paniers_table)
        )

        with self.connection.cursor(name="server_cursor_paniers") as cursor:
            cursor.execute(query_paniers)
            total = 0

            while True:
                paniers = cursor.fetchmany(batch_size)
                if not paniers:
                    break

                profiles = self._convert_paniers_to_profiles(paniers, voeu_to_formations)
                total += len(profiles)
                if profiles:
                    yield profiles

        LOGGER.info(f"Loaded {total} profiles from paniers de voeux.")

    def _load_voeu_to_formations_mapping(
        self, join_table: str
    ) -> Dict[str, List[str]]:
        """Load the mapping voeu -> formations."""
        LOGGER.info(f"Loading voeu to formations mapping from '{join_table}'...")
        query_mapping = SQL("SELECT id_formation, id_voeu FROM {}").format(
            Identifier(join_table)
        )
        
        # TODO: vérifier s'il est possible d'avoir plusieurs formations par voeu
        # si ce n'est pas le cas on peut simplifier et utiliser Dict[str, str] au lieu de Dict[str, List[str]]
        voeu_to_formations: Dict[str, List[str]] = defaultdict(list)
        with self.connection.cursor() as cursor:
            cursor.execute(query_mapping)
            rows = cursor.fetchall()
            for row in rows:
                voeu_to_formations[row["id_voeu"]].append(row["id_formation"])
        
        LOGGER.info(f"Loaded mapping for {len(voeu_to_formations)} voeux.")
        return voeu_to_formations

    def _convert_paniers_to_profiles(
        self,
        paniers_rows: list,
        voeu_to_formations: Dict[str, List[str]],
    ) -> list[Profile]:
        """Converts wish baskets rows to Profiles."""
        profiles: list[Profile] = []
        
        for row in paniers_rows:
            voeux = row["voeux"] if row["voeux"] else []
            formations_set: set[str] = set()
            
            for voeu in voeux:
                if voeu in voeu_to_formations:
                    formations_set.update(voeu_to_formations[voeu])

            if formations_set:
                formations = list(formations_set)
                profile = Profile(
                    features=[
                        Item(value=fl, category="formations_favorites")
                        for fl in formations
                    ],
                    targets=formations,
                )
                profiles.append(profile)

        return profiles


def get_env_or_raise(var_name: str) -> str:
    res = os.getenv(var_name)
    if res is None:
        raise RuntimeError(f"Missing environment variable: '{var_name}'")
    return res


### Helper functions for parsing profiles
def parse_formations(val: List[Dict[str, Any]] | None) -> List[str]:
    """
    Extract the IDs of the formations
    """
    return [f[JsonNestedFields.FORMATION_ID] for f in val or []]


def parse_voeux(val: List[Dict[str, Any]] | None) -> List[str]:
    """
    Extract the IDs of the "parcoursup voeux"
    """
    return [v[JsonNestedFields.VOEU_ID] for v in val or []]


def parse_communes(val: List[Dict[str, Any]] | None) -> List[str]:
    """
    Extract the INSEE Code of the cities
    """
    return [c[JsonNestedFields.COMMUNE_CODE_INSEE] for c in val or []]


def none_to_empty_list(val: List[Any] | None) -> List[Any]:
    return val or []


class StudentDbRow(BaseModel):
    id: str = Field(alias=DbColumns.ID)
    situation: str | None = Field(alias=DbColumns.SITUATION)
    classe: str | None = Field(alias=DbColumns.CLASSE)
    id_baccalaureat: str | None = Field(alias=DbColumns.ID_BACCALAUREAT)
    duree_etudes_prevue: str | None = Field(alias=DbColumns.DUREE_ETUDES_PREVUE)
    alternance: str | None = Field(alias=DbColumns.ALTERNANCE)
    specialites: Annotated[List[str], BeforeValidator(none_to_empty_list)] = Field(
        default=[], alias=DbColumns.SPECIALITES
    )
    domaines: Annotated[List[str], BeforeValidator(none_to_empty_list)] = Field(
        default=[], alias=DbColumns.DOMAINES
    )
    centres_interets: Annotated[List[str], BeforeValidator(none_to_empty_list)] = Field(
        default=[], alias=DbColumns.CENTRES_INTERETS
    )
    metiers_favoris: Annotated[List[str], BeforeValidator(none_to_empty_list)] = Field(
        default=[], alias=DbColumns.METIERS_FAVORIS
    )
    corbeille_formations: Annotated[List[str], BeforeValidator(none_to_empty_list)] = (
        Field(default=[], alias=DbColumns.CORBEILLE_FORMATIONS)
    )
    communes_favorites: Annotated[List[str], BeforeValidator(parse_communes)] = Field(
        default=[], alias=DbColumns.COMMUNES_FAVORITES
    )
    formations_favorites: Annotated[List[str], BeforeValidator(parse_formations)] = (
        Field(default=[], alias=DbColumns.FORMATIONS_FAVORITES)
    )
    voeux_favoris: Annotated[List[str], BeforeValidator(parse_voeux)] = Field(
        default=[], alias=DbColumns.VOEUX_FAVORIS
    )

    def to_profile(self, config: ProfileConfig) -> Profile:
        items: list[Item] = []

        def push_if_not_none(cond: bool, i: list[Item], v: str | None, c: str):
            if v is not None and cond:
                i.append(Item(value=v, category=c))

        push_if_not_none(config.use_situation, items, self.situation, "situation")
        push_if_not_none(config.use_classe, items, self.classe, "classe")
        push_if_not_none(
            config.use_id_baccalaureat, items, self.id_baccalaureat, "id_baccalaureat"
        )
        push_if_not_none(
            config.use_duree_etudes_prevue,
            items,
            self.duree_etudes_prevue,
            "duree_etudes_prevue",
        )
        push_if_not_none(config.use_alternance, items, self.alternance, "alternance")

        def push_list(cond: bool, i: list[Item], l: list[str], c: str):  # noqa: E741
            if cond:
                i += [Item(value=v, category=c) for v in l]

        push_list(config.use_specialites, items, self.specialites, "specialites")
        push_list(config.use_interests, items, self.domaines, "interests")
        push_list(config.use_interests, items, self.centres_interets, "interests")
        push_list(
            config.use_metiers_favoris, items, self.metiers_favoris, "metiers_favoris"
        )
        push_list(
            config.use_corbeille_formations,
            items,
            self.corbeille_formations,
            "corbeille_formations",
        )
        push_list(
            config.use_communes_favorites,
            items,
            self.communes_favorites,
            "communes_favorites",
        )
        push_list(
            config.use_formations_favorites,
            items,
            self.formations_favorites,
            "formations_favorites",
        )
        push_list(config.use_voeux_favoris, items, self.voeux_favoris, "voeux_favoris")

        targets = config.filter_targets(items)
        return Profile(features=items, targets=targets)
