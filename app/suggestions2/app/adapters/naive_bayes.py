from collections import defaultdict
from typing import Dict, List, Tuple

import numpy as np
import pandas as pd

from app.domain.models.explanation import Explanation, Explanations
from app.domain.models.profile import Profile
from app.domain.models.suggestion import Suggestions
from app.domain.ports.suggestion import ExplainableSuggestionsEngine


class NaiveBayesMatrix(ExplainableSuggestionsEngine):
    def __init__(
        self,
        regularization_laplace: float = 1.0,
    ):
        self.matrix: pd.DataFrame = pd.DataFrame()
        self.explanation_matrix: pd.DataFrame = pd.DataFrame()
        self.explanation_popularity: pd.Series = pd.Series()
        self.regularization_laplace = regularization_laplace

    def init_from_profiles(self, profiles: list[Profile]):
        self.matrix = compute_naive_bayes_matrix(profiles, self.regularization_laplace)
        self.explanation_matrix, self.explanation_popularity = compute_explanation_matrix(
            self.matrix
        )

    def suggest(self, profile: Profile) -> Suggestions:
        scores = predict_naive_bayes(self.matrix, profile.features_str())

        return Suggestions(scores=scores)

    def explain(self, profile: Profile, keys: list[str]) -> Explanations:
        scores = explain_naive_bayes(
            self.explanation_matrix, profile, keys, self.explanation_popularity
        )
        return Explanations(expls=scores)

    def __str__(self) -> str:
        return str(self.matrix)


def compute_naive_bayes_matrix(
    profiles: list[Profile],
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
    n_profiles: int = len(profiles)
    all_key_values: set[str] = set()

    # Count occurrences and co-occurrences
    for profile in profiles:
        for t in profile.targets:
            occurences_target[t] += 1

            for v in profile.features_str():
                all_key_values.add(v)
                co_occurences_target_key[(t, v)] += 1

    # Compute the conditional probabilities P(t | v)
    dict_bayes: Dict[str, Dict[str, float]] = defaultdict(lambda: defaultdict(float))
    for t, count_t in occurences_target.items():
        for v in all_key_values:
            count_v_and_t = co_occurences_target_key[(t, v)]
            # Apply Laplace regularization
            dict_bayes[t][v] = (count_v_and_t + regularization_laplace) / (
                count_t + regularization_laplace
            )
    bayes_df = pd.DataFrame(dict_bayes)

    # Compute the frequency of each target value, and add it as a "bias" row
    probabilites_items = {item: occurences_target[item] / n_profiles for item in occurences_target}
    bias_row = pd.Series(
        [probabilites_items[item] for item in bayes_df.columns],  # type: ignore
        index=bayes_df.columns,
        name="bias",
    )
    bayes_df = pd.concat([bayes_df, bias_row.to_frame().T])

    return bayes_df


def predict_naive_bayes(matrix: pd.DataFrame, items: List[str]) -> Dict[str, float]:
    # Ignore items that are not in the matrix's index
    items = [it for it in items if it in matrix.index]
    items.append("bias")
    scores: pd.Series[float] = (
        matrix.loc[items].prod(axis=0).pow(1 / len(items))
    )  # Apply pow 1/len(items) to scale the scores

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
    explain_matrix: pd.DataFrame, profile: Profile, keys: List[str], popularity_matrix: pd.Series
) -> Dict[str, Explanation]:
    # Ignore items/keys that are not in the matrix's index/columns
    items = [it for it in profile.features_str() if it in explain_matrix.index]
    keys = [k for k in keys if k in explain_matrix.columns]

    scores: Dict[str, Dict[str, float]] = explain_matrix[keys].loc[items].to_dict()  # type: ignore
    popularity: Dict[str, float] = popularity_matrix[keys].to_dict()
    return {
        k: Explanation(
            key=k,
            popularity=popularity[k],
            relative_frequency={
                it: scores[k][str(it)] for it in profile.features if str(it) in scores[k]
            },
        )
        for k in scores
    }
