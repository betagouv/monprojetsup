from typing import List

import numpy as np
from app.data_types import Basket, DatasetStudents, Student, encode_item
from app.naive_bayes.matrix import (
    NaiveBayesMatrix,
    compute_explanation_matrix,
    compute_naive_bayes_matrix,
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
