import psycopg
from dotenv import dotenv_values
from psycopg.rows import TupleRow
from psycopg import Connection


def connect_to_db() -> Connection[TupleRow]:
    """
    Connect to the database using the credentials in the .env file.
    """
    config = dotenv_values(".env")
    try:
        conn = psycopg.connect(
            f"postgresql://{config['DB_HOST']}:{config['DB_PORT']}?user={config['DB_USERNAME']}&password={config['DB_PASSWORD']}&dbname={config['DB_NAME']}",
        )
        return conn
    except KeyError as e:
        print("Missing env variable:", e)
        exit(1)


def _execute_query(query: str, conn: Connection[TupleRow]) -> list[TupleRow]:
    with conn.cursor() as cur:
        cur.execute(query)
        return cur.fetchall()


def _load_from_table(
    conn: Connection[TupleRow],
    columns: list[str],
    table: str,
    extra: str | None = None,
    labels: list[str] | None = None,
) -> dict[str, dict[str, str]]:
    """
    Helper function to fetch the data in the given columns in the target table,
    in pseudo-json format.
    The outer dictionary uses the first column as the key,
    and the inner dictionary is of the form `{column: value}`.
    """
    if labels is None:
        labels = columns
    else:
        assert len(labels) == len(
            columns
        ), "columns and labels must have the same length"
    return {
        row[0]: {labels[i]: row[i] for i in range(len(columns))}
        for row in _execute_query(
            f"""
            SELECT {','.join(columns)}
            FROM {table}
            {extra if extra is not None else ""}
            """,
            conn,
        )
    }


def load_formations(conn: Connection[TupleRow]) -> dict[str, dict[str, str]]:
    return _load_from_table(
        conn,
        ["id", "label", "descriptif_diplome", "descriptif_general"],
        "ref_formation",
    )


def load_domaines(conn: Connection[TupleRow]) -> dict[str, dict[str, str]]:
    return _load_from_table(
        conn,
        ["id", "nom", "description"],
        "ref_domaine",
    )


def _load_extended_edges(conn: Connection[TupleRow]) -> dict[str, dict[str, str]]:
    """
    Loads edges between formations and T-IDEO, converting them into
    formation - domain edges using the `ref_domaine_ideo` table.
    """
    return _load_from_table(
        conn,
        ["sugg_edges.id", "src", "id_domaine_mps"],
        "sugg_edges",
        extra="""
    INNER JOIN ref_domaine_ideo
    ON sugg_edges.dst = ref_domaine_ideo.id""",
        labels=["id", "src", "dst"],
    )


def _load_basic_edges(conn: Connection[TupleRow]) -> dict[str, dict[str, str]]:
    return _load_from_table(
        conn,
        ["id", "dst", "src"],
        "sugg_edges",
        extra="""
        WHERE ((src LIKE 'fl%' OR src LIKE 'fr%') AND dst LIKE 'dom%') OR
        ((dst LIKE 'fl%' OR dst LIKE 'fr%') AND src LIKE 'dom%')""",
    )


def load_edges(conn: Connection[TupleRow]) -> set[tuple[str, str]]:
    basic = _load_basic_edges(conn)
    extended = _load_extended_edges(conn)
    return {(basic[e]["src"], basic[e]["dst"]) for e in basic} | {
        (extended[e]["src"], extended[e]["dst"]) for e in extended
    }
