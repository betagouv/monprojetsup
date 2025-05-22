import logging
import os
from typing import List
from urllib.parse import quote_plus
import psycopg as pg
from psycopg.rows import dict_row, DictRow, class_row
from psycopg.sql import SQL, Identifier
from dotenv import load_dotenv

from .types import StudentDbRow
from app.data_types import Basket, DatasetStudents, Student

logger = logging.getLogger("uvicorn.error.app")

load_dotenv()


def create_connection_from_env() -> pg.Connection[DictRow]:
    DB_HOSTNAME = os.getenv("DB_SUGGESTIONS2_HOSTNAME", default="localhost")
    DB_PORT = os.getenv("DB_SUGGESTIONS2_PORT")
    DB_USERNAME = os.getenv("DB_SUGGESTIONS2_USERNAME")
    DB_PASSWORD = os.getenv("DB_SUGGESTIONS2_PASSWORD")
    DB_NAME = os.getenv("DB_SUGGESTIONS2_NAME")
    logger.info(
        "Connecting to database '%s' at address '%s:%s' with username '%s'.",
        DB_NAME,
        DB_HOSTNAME,
        DB_PORT,
        DB_USERNAME,
    )

    encoded_username = quote_plus(DB_USERNAME)
    encoded_pw = quote_plus(DB_PASSWORD)
    encoded_dbname = quote_plus(DB_NAME)

    connection_uri = f"postgresql://{DB_HOSTNAME}:{DB_PORT}?user={encoded_username}&password={encoded_pw}&dbname={encoded_dbname}"

    return pg.connect(
        connection_uri,
        row_factory=dict_row,  # type: ignore
    )  # type: ignore


def _fetch_students_data(conn: pg.Connection[DictRow]) -> List[StudentDbRow]:
    TABLE_NAME = os.getenv("DB_SUGGESTIONS2_PROFIL_TABLE", default="profil_reference")
    logger.info("Loading data from table '%s'.", TABLE_NAME)

    query = SQL("SELECT * FROM {}").format(Identifier(TABLE_NAME))
    with conn.cursor(row_factory=class_row(StudentDbRow)) as cursor:
        cursor.execute(query)
        students_data = cursor.fetchall()
    return students_data


def load_students_dataset() -> DatasetStudents:
    with create_connection_from_env() as conn:
        students_data = _fetch_students_data(conn)
    students = [
        Student(id=student_data.id, basket=Basket.from_pg_row(student_data))
        for student_data in students_data
    ]
    return DatasetStudents(students)
