import os
from typing import Annotated, Any, Dict, List, Literal
from urllib.parse import quote_plus

import psycopg as pg
from dotenv import load_dotenv
from psycopg.rows import DictRow, class_row, dict_row
from psycopg.sql import SQL, Identifier
from pydantic import BaseModel, BeforeValidator, Field

from app.config import LOGGER
from app.db_schema import DbColumns, JsonNestedFields
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

    def load_profiles(self, table_name: str, config: ProfileConfig) -> list[Profile]:
        query = SQL("SELECT * FROM {}").format(Identifier(table_name))
        with self.connection.cursor(row_factory=class_row(StudentDbRow)) as cursor:
            cursor.execute(query)
            students_data = cursor.fetchall()
        return [s.to_profile(config) for s in students_data]


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
