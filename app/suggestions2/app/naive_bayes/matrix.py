import logging
import time
from collections import defaultdict
from typing import Dict, List, Tuple

import numpy as np
import pandas as pd

from app.data_types import Basket, DatasetStudents, ItemSide, decode_item
from app.database.db import load_students_dataset

LOGGER = logging.getLogger("uvicorn.error.app")


def load_data_and_create_matrix() -> "NaiveBayesMatrix":
    LOGGER.info("Loading students from db.")
    students = load_students_dataset()

    LOGGER.info("Creating NaiveBayes Matrix.")
    start_time = time.time()
    m = NaiveBayesMatrix()
    m.fit(students)
    elapsed = time.time() - start_time
    LOGGER.debug("NaiveBayesMatrix::fit() took %f s.", elapsed)

    return m


class NaiveBayesMatrix:
    def __init__(
        self,
        regularization_laplace: float = 1.0,
    ):
        self.matrix: pd.DataFrame = pd.DataFrame()
        self.explanation_matrix: pd.DataFrame = pd.DataFrame()
        self.explanation_popularity: pd.Series = pd.Series()
        self.regularization_laplace = regularization_laplace

    def fit(self, dataset: DatasetStudents) -> "NaiveBayesMatrix":
        self.matrix = compute_naive_bayes_matrix(dataset, self.regularization_laplace)
        self.explanation_matrix, self.explanation_popularity = compute_explanation_matrix(
            self.matrix
        )
        return self

    def explain(
        self, basket: Basket, keys: List[str]
    ) -> Tuple[Dict[str, Dict[Tuple[str, ItemSide], float]], Dict[str, float]]:
        return explain_naive_bayes(
            self.explanation_matrix, basket.get_items(), keys, self.explanation_popularity
        )

    def predict(self, basket: Basket) -> Dict[str, float]:
        return predict_naive_bayes(self.matrix, basket.get_items())

    def predict_top_k(self, basket: Basket, k: int) -> List[Tuple[str, float]]:
        scores = self.predict(basket)
        return sorted(scores.items(), key=lambda x: x[1], reverse=True)[:k]

    def __str__(self) -> str:
        return str(self.matrix)


N_FEATURES = 3  # Each feature has 3 possible states: positive, negative or absent.


def compute_naive_bayes_matrix(
    dataset: DatasetStudents,
    regularization_laplace: float,
) -> pd.DataFrame:
    """
    Compute a Naive Bayes matrix containing the probabilities P(t | v)
    for each value `t` of `target`
    and each value `v` that each attribute in `keys` takes in the dataset.

    `regularization_laplace`: Parameter alpha for regularization,
    see [https://scikit-learn.org/stable/modules/naive_bayes.html#categorical-naive-bayes](here).
    """
    co_occurences_target_key: Dict[Tuple[str, str], int] = defaultdict(int)
    occurences_target: Dict[str, int] = defaultdict(int)
    n_students: int = len(dataset)
    all_key_values: set[str] = set()

    # Count occurrences and co-occurrences
    for student in dataset:
        basket = student.basket
        for t in basket.get_targets():
            occurences_target[t] += 1

            for v in basket.get_items():
                all_key_values.add(v)
                co_occurences_target_key[(t, v)] += 1

    # Compute the conditional probabilities P(t | v)
    dict_bayes: Dict[str, Dict[str, float]] = defaultdict(lambda: defaultdict(float))
    for t, count_t in occurences_target.items():
        for v in all_key_values:
            count_v_and_t = co_occurences_target_key[(t, v)]
            # Apply Laplace regularization
            dict_bayes[t][v] = (count_v_and_t + regularization_laplace) / (
                count_t + regularization_laplace * N_FEATURES
            )
    bayes_df = pd.DataFrame(dict_bayes)

    # Compute the frequency of each target value, and add it as a "bias" row
    probabilites_items = {item: occurences_target[item] / n_students for item in occurences_target}
    bias_row = pd.Series(
        [probabilites_items[item] for item in bayes_df.columns],
        index=bayes_df.columns,
        name="bias",
    )
    bayes_df = pd.concat([bayes_df, bias_row.to_frame().T])

    return bayes_df


def predict_naive_bayes(matrix: pd.DataFrame, items: List[str]) -> Dict[str, float]:
    # Ignore items that are not in the matrix's index
    items = [it for it in items if it in matrix.index]
    items.append("bias")
    scores: pd.Series[float] = matrix.loc[items].prod(axis=0)

    return {t: float(scores[t]) for t in scores.index}


def compute_explanation_matrix(matrix: pd.DataFrame) -> Tuple[pd.DataFrame, pd.Series]:
    """
    Computes the "explanation scores" matrix of the given naive bayes matrix.

    This matrix contains the log2 conditional probabilities of the naive bayes matrix,
    centered relative to the average, for each feature.
    Therefore, for a given target `t`, this number for feature `f` is larger than 0
    when P(f|t) is greater than the geometric mean of the P(f|t') over all t,
    and smaller otherwise.
    """
    bias_row: pd.Series[float] = np.log2(matrix.loc["bias"])  # type: ignore
    bias_row = bias_row - bias_row.mean()

    matrix = matrix.drop(["bias"], axis=0, inplace=False)
    log_probs: pd.DataFrame = np.log2(matrix)  # type: ignore
    log_probs = log_probs.apply(lambda row: row - row.mean(), axis=1)  # type: ignore
    return log_probs, bias_row


def explain_naive_bayes(
    explain_matrix: pd.DataFrame, items: List[str], keys: List[str], popularity_matrix: pd.Series
) -> Tuple[Dict[str, Dict[Tuple[str, ItemSide], float]], Dict[str, float]]:
    # Ignore items/keys that are not in the matrix's index/columns
    items = [it for it in items if it in explain_matrix.index]
    keys = [k for k in keys if k in explain_matrix.columns]

    scores: Dict[str, Dict[str, float]] = explain_matrix[keys].loc[items].to_dict()  # type: ignore
    popularity: Dict[str, float] = popularity_matrix[keys].to_dict()
    return {k: {decode_item(it): s for (it, s) in scores[k].items()} for k in scores}, popularity
