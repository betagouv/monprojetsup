import logging
import os
from typing import List
import psycopg as pg
from psycopg.rows import dict_row, DictRow, class_row
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
    return pg.connect(
        f"postgresql://{DB_HOSTNAME}:{DB_PORT}?user={DB_USERNAME}&password={DB_PASSWORD}&dbname={DB_NAME}",
        row_factory=dict_row,  # type: ignore
    )  # type: ignore


def _fetch_students_data(conn: pg.Connection[DictRow]) -> List[StudentDbRow]:
    query = "SELECT * FROM profil_eleve"
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
