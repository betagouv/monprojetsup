from app.adapters.naive_bayes import NaiveBayesMatrix
from app.domain.models.profile import Item, Profile


def test_naive_bayes():
    profiles = [
        Profile(
            features=[Item(category="c1", value="v1"), Item(category="c2", value="v2")],
            targets=["v1", "v2", "v3"],
        ),
        Profile(
            features=[Item(category="c1", value="v1"), Item(category="c2", value="v3")],
            targets=["v1", "v2"],
        ),
        Profile(
            features=[Item(category="c1", value="v2"), Item(category="c2", value="v3")],
            targets=["v2", "v3"],
        ),
    ]

    matrix = NaiveBayesMatrix(
        regularization_laplace=1.0,
    )

    matrix.init_from_profiles(profiles)

    res = matrix.suggest(
        Profile(
            features=[Item(category="c1", value="v1"), Item(category="c2", value="v2")],
            targets=[],
        )
    )

    assert "v1" in res.scores
    assert "v2" in res.scores
    assert "v3" in res.scores
    assert res.scores["v1"] > 0.0
    assert res.scores["v2"] > 0.0
    assert res.scores["v3"] > 0.0
    assert res.scores["v1"] <= 1.0
    assert res.scores["v2"] <= 1.0
    assert res.scores["v3"] <= 1.0


def test_explanation():
    profiles = [
        Profile(
            features=[Item(category="c1", value="v1"), Item(category="c2", value="v2")],
            targets=["v1", "v2", "v3"],
        ),
        Profile(
            features=[Item(category="c1", value="v1"), Item(category="c2", value="v3")],
            targets=["v1", "v2"],
        ),
        Profile(
            features=[Item(category="c1", value="v2"), Item(category="c2", value="v3")],
            targets=["v2", "v3"],
        ),
    ]

    matrix = NaiveBayesMatrix(
        regularization_laplace=1.0,
    )

    matrix.init_from_profiles(profiles)

    res = matrix.explain(
        Profile(
            features=[Item(category="c1", value="v1"), Item(category="c2", value="v2")],
            targets=[],
        ),
        keys=["v1", "v2", "v3", "v4"],
    )

    assert "v1" in res.expls
    assert "v2" in res.expls
    assert "v3" in res.expls
    assert "v4" not in res.expls
