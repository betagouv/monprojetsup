from typing import List

import numpy as np
from app.data_types import Basket, DatasetStudents, Student, encode_item
from app.naive_bayes.matrix import (
    NaiveBayesMatrix,
    compute_explanation_matrix,
    compute_naive_bayes_matrix,
    predict_naive_bayes,
)
import pandas as pd


def student_from_indices(
    id: str,
    tgt_ids: List[int],
    targets: List[str],
    pos_ids: List[int],
    neg_ids: List[int],
    features: List[str],
) -> Student:
    return Student(
        id=id,
        basket=Basket(
            targets=[targets[i] for i in tgt_ids],
            items=[encode_item(features[i]) for i in pos_ids]
            + [encode_item(features[i], False) for i in neg_ids],
        ),
    )


def test_compute_matrix() -> None:
    targets = ["fa", "ma", "da"]
    features = ["f1", "m1", "d1"]
    data = [
        student_from_indices("s1", [1, 2], targets, [0, 2], [1], features),
        student_from_indices("s2", [0, 2], targets, [0], [1, 2], features),
        student_from_indices("s3", [1], targets, [0, 1, 2], [], features),
        student_from_indices("s4", [0, 1], targets, [2], [0, 1], features),
    ]

    dataset = DatasetStudents(data)

    matrix = compute_naive_bayes_matrix(dataset, regularization_laplace=0)

    expected_matrix = pd.DataFrame(
        {
            targets[0]: [
                1 / 2,
                0 / 2,
                1 / 2,  #
                1 / 2,
                2 / 2,
                1 / 2,  #
                2 / 4,
            ],
            targets[1]: [
                2 / 3,
                1 / 3,
                3 / 3,  #
                1 / 3,
                2 / 3,
                0 / 3,  #
                3 / 4,
            ],
            targets[2]: [
                2 / 2,
                0 / 2,
                1 / 2,  #
                0 / 2,
                2 / 2,
                1 / 2,  #
                2 / 4,
            ],
        },
        index=[encode_item(item) for item in features]
        + [encode_item(item, False) for item in features]
        + ["bias"],
    )
    pd.testing.assert_frame_equal(matrix.reindex_like(expected_matrix), expected_matrix)


def test_compute_matrix_laplace() -> None:
    """Test compute_naive_bayes_matrix with regularization_laplace > 0."""
    targets = ["A", "B"]
    features = ["x", "y"]
    # Dataset:
    # Student 1: Target A, Items: x_pos, y_pos
    # Student 2: Target B, Items: x_neg, y_neg
    # Student 3: Target A, Items: x_pos
    data = [
        student_from_indices("s1", [0], targets, [0, 1], [], features),
        student_from_indices("s2", [1], targets, [], [0, 1], features),
        student_from_indices("s3", [0], targets, [0], [], features),
    ]
    dataset = DatasetStudents(data)
    laplace = 1.0  # Use Laplace smoothing

    matrix = compute_naive_bayes_matrix(dataset, regularization_laplace=laplace)

    # Expected calculations with Laplace=1, N_FEATURES=3
    # Occurrences: A=2, B=1, Total Students=3
    # Co-occurrences:
    # (A, x_pos) = 2, (A, x_neg) = 0, (A, y_pos) = 1, (A, y_neg) = 0
    # (B, x_pos) = 0, (B, x_neg) = 1, (B, y_pos) = 0, (B, y_neg) = 1

    # P(x_pos | A) = (2+1)/(2+1*3) = 3/5 = 0.6
    # P(x_neg | A) = (0+1)/(2+1*3) = 1/5 = 0.2
    # P(y_pos | A) = (1+1)/(2+1*3) = 2/5 = 0.4
    # P(y_neg | A) = (0+1)/(2+1*3) = 1/5 = 0.2

    # P(x_pos | B) = (0+1)/(1+1*3) = 1/4 = 0.25
    # P(x_neg | B) = (1+1)/(1+1*3) = 2/4 = 0.5
    # P(y_pos | B) = (0+1)/(1+1*3) = 1/4 = 0.25
    # P(y_neg | B) = (1+1)/(1+1*3) = 2/4 = 0.5

    # Bias (Prior Probability):
    # P(A) = 2/3
    # P(B) = 1/3

    expected_matrix = pd.DataFrame(
        {
            targets[0]: [0.6, 0.4, 0.2, 0.2, 2 / 3],  # x_pos, y_pos, x_neg, y_neg, bias
            targets[1]: [0.25, 0.25, 0.5, 0.5, 1 / 3],  # x_pos, y_pos, x_neg, y_neg, bias
        },
        index=[encode_item(item) for item in features]
        + [encode_item(item, False) for item in features]
        + ["bias"],
    )
    pd.testing.assert_frame_equal(matrix.reindex_like(expected_matrix), expected_matrix, atol=1e-9)


def test_compute_matrix_empty_dataset() -> None:
    """Test compute_naive_bayes_matrix with an empty dataset."""
    dataset = DatasetStudents([])
    laplace = 1.0
    matrix = compute_naive_bayes_matrix(dataset, regularization_laplace=laplace)

    # Expect an empty DataFrame with only the 'bias' row if no features/targets are seen
    # or an empty DataFrame if the logic handles it by not having any columns/index
    # Based on the code, if no students, occurences_target is empty, all_key_values is empty.
    # dict_bayes will be empty. bias_row will be based on empty occurences_target.
    # The resulting DataFrame should contain just an empty row.
    expected_matrix = pd.DataFrame(index=["bias"])
    pd.testing.assert_frame_equal(matrix, expected_matrix)


def test_predict_naive_bayes_empty_basket() -> None:
    """Test predict_naive_bayes with an empty basket."""
    targets = ["A", "B"]
    features = ["x", "y"]
    data = [
        student_from_indices("s1", [0], targets, [0], [], features),
        student_from_indices("s2", [1], targets, [1], [], features),
    ]
    dataset = DatasetStudents(data)
    matrix = compute_naive_bayes_matrix(dataset, regularization_laplace=1.0)

    empty_basket_items = []
    scores = predict_naive_bayes(matrix, empty_basket_items)

    # With an empty basket, only the bias row should be used for prediction
    expected_scores = {target: matrix.loc["bias", target] for target in targets}
    assert scores == expected_scores


def test_predict_naive_bayes_unknown_items() -> None:
    """Test predict_naive_bayes with items not in the matrix index."""
    targets = ["A", "B"]
    features = ["x", "y"]
    data = [
        student_from_indices("s1", [0], targets, [0], [], features),
        student_from_indices("s2", [1], targets, [1], [], features),
    ]
    dataset = DatasetStudents(data)
    matrix = compute_naive_bayes_matrix(dataset, regularization_laplace=1.0)

    # Basket with items not in the training data
    unknown_items = [encode_item("z"), encode_item("unknown_feature", False)]
    scores = predict_naive_bayes(matrix, unknown_items)

    # Only the bias should contribute as unknown items are ignored
    expected_scores = {target: matrix.loc["bias", target] for target in targets}
    assert scores == expected_scores


def test_predict_naive_bayes_mixed_items() -> None:
    """Test predict_naive_bayes with a mix of known and unknown items."""
    targets = ["A", "B"]
    features = ["x", "y"]
    data = [
        student_from_indices("s1", [0], targets, [0], [], features),
        student_from_indices("s2", [1], targets, [1], [], features),
    ]
    dataset = DatasetStudents(data)
    matrix = compute_naive_bayes_matrix(dataset, regularization_laplace=1.0)

    # Basket with known and unknown items
    mixed_items = [encode_item("x"), encode_item("unknown_feature", False)]
    scores = predict_naive_bayes(matrix, mixed_items)

    # Prediction should use known items ('x_pos') and the bias
    # Expected score for target T is matrix.loc['x_pos', T] * matrix.loc['bias', T]
    expected_scores = {
        target: matrix.loc[encode_item("x"), target] * matrix.loc["bias", target]
        for target in targets
    }

    assert np.allclose(list(scores.values()), list(expected_scores.values()))
    assert list(scores.keys()) == list(expected_scores.keys())


def test_explanation_matrix_has_zero_mean_on_rows() -> None:
    targets = ["fa", "ma", "da"]
    features = ["f1", "m1", "d1"]
    data = [
        student_from_indices("s1", [1, 2], targets, [0, 2], [1], features),
        student_from_indices("s2", [0, 2], targets, [0], [1, 2], features),
        student_from_indices("s3", [1], targets, [0, 1, 2], [], features),
        student_from_indices("s4", [0, 1], targets, [2], [0, 1], features),
    ]

    dataset = DatasetStudents(data)

    matrix = compute_naive_bayes_matrix(dataset, regularization_laplace=1)
    explanation_matrix, popularity = compute_explanation_matrix(matrix)
    avg = explanation_matrix.mean(axis=1)
    assert np.allclose(avg, 0)
    assert np.allclose(popularity.mean(), 0)


def test_explanations() -> None:
    targets = ["fa", "ma", "da"]
    features = ["f1", "m1", "d1"]
    data = [
        student_from_indices("s1", [1, 2], targets, [0, 2], [1], features),
        student_from_indices("s2", [0, 2], targets, [0], [1, 2], features),
        student_from_indices("s3", [1], targets, [0, 1, 2], [], features),
        student_from_indices("s4", [0, 1], targets, [2], [0, 1], features),
    ]

    dataset = DatasetStudents(data)

    matrix = NaiveBayesMatrix(regularization_laplace=1)
    matrix.fit(dataset)

    s = student_from_indices("s5", [], targets, [0, 1], [2], features)
    expl, popularity = matrix.explain(s.basket, ["fa", "ma"])

    assert "fa" in expl
    assert "ma" in expl

    assert expl["fa"][("f1", "positive")] < expl["ma"][("f1", "positive")]
    assert expl["fa"][("m1", "positive")] < expl["ma"][("m1", "positive")]
    assert expl["fa"][("d1", "negative")] > expl["ma"][("d1", "negative")]

    assert "fa" in popularity
    assert "ma" in popularity

    assert popularity["fa"] < popularity["ma"]
