"""
Tests de suggestions2 avec DB et CSV mockées.
"""

import pytest
from pathlib import Path
from unittest.mock import patch, MagicMock

from fastapi.testclient import TestClient

from app.domain.models.profile import Item, Profile

FAKE_MODELS_DIR = Path(__file__).parent / "fixtures" / "models"

FAKE_PROFILES = [
    Profile(
        features=[
            Item(category="interests", value="dom1"),
            Item(category="formations_favorites", value="fl1"),
        ],
        targets=["fl1"],
    ),
    Profile(
        features=[
            Item(category="specialites", value="sp1"),
            Item(category="formations_favorites", value="fl2"),
        ],
        targets=["fl2"],
    ),
    Profile(
        features=[
            Item(category="interests", value="dom2"),
            Item(category="formations_favorites", value="fl3"),
        ],
        targets=["fl3"],
    ),
]


@pytest.fixture(scope="session")
def fastapi_app():
    mock_db = MagicMock()
    mock_db.iter_profiles_batched.side_effect = lambda *args, **kwargs: iter(
        [FAKE_PROFILES]
    )

    with (
        patch(
            "app.adapters.pg_database.PostgresDatabase.from_env",
            return_value=mock_db,
        ),
        patch(
            "app.config.DEFAULT_MODELS_DIR",
            FAKE_MODELS_DIR,
        ),
    ):
        from app.setup_fastapi import app

        return app


class TestSetupFastapi:
    @pytest.fixture
    def client(self, fastapi_app):
        return TestClient(fastapi_app)

    def test_health_check(self, client):
        response = client.get("/health")
        assert response.status_code == 200
        assert response.json() == {"status": "OK"}

    def test_suggestions_empty_profile(self, client):
        response = client.post(
            "/suggestions",
            json={"profile": {}, "keys": ["fl1", "fl2", "fl3"]},
        )
        assert response.status_code == 200
        data = response.json()
        assert data["header"]["status"] == 0
        assert len(data["scores"]) == 3

    def test_explanations_endpoint(self, client):
        response = client.post(
            "/explanations",
            json={
                "profile": {
                    "interests": ["dom1"],
                    "spe_classes": ["sp1"],
                },
                "keys": ["fl1", "fl2"],
            },
        )
        assert response.status_code == 200
        data = response.json()
        assert data["header"]["status"] == 0
        assert len(data["explanations"]) > 0
        assert len(data["scores"]) == 2

    def test_suggestions_with_profile(self, client):
        response = client.post(
            "/suggestions",
            json={
                "profile": {
                    "bac": "Générale",
                    "interests": ["dom1"],
                    "spe_classes": ["sp1"],
                    "choix": [
                        {"id": "fl1", "status": 1},
                        {"id": "met1", "status": 1},
                    ],
                },
                "keys": ["fl1", "fl2", "fl3"],
            },
        )
        assert response.status_code == 200
        data = response.json()
        assert data["header"]["status"] == 0
        # Le profil a des features connues du modèle (dom1, sp1), donc au moins un score doit être > 0
        all_scores = [
            score_val
            for score in data["scores"]
            for score_val in score["scores"].values()
        ]
        assert any(s > 0 for s in all_scores)

    def test_suggestions_with_unknown_keys(self, client):
        response = client.post(
            "/suggestions",
            json={
                "profile": {"interests": ["dom1"]},
                "keys": ["formation_inexistante"],
            },
        )
        assert response.status_code == 200
        data = response.json()
        assert len(data["scores"]) == 1
        # les trois scores doivent être à 0 car la clé "formation_inexistante" n'est pas dans le modèle
        assert all(v == 0.0 for v in data["scores"][0]["scores"].values())

    def test_explanations_with_unknown_keys(self, client):
        response = client.post(
            "/explanations",
            json={
                "profile": {"interests": ["dom1"]},
                "keys": ["formation_inexistante"],
            },
        )
        assert response.status_code == 200
        data = response.json()
        assert "explanations" in data
        assert "scores" in data

    def test_malformed_request_returns_422(self, client):
        response = client.post(
            "/suggestions",
            json={"profile": "not_valid_profile", "keys": ["fl1"]},
        )
        assert response.status_code == 422

    def test_naive_bayes_matrices_structure(self, fastapi_app):
        import app.setup_fastapi as setup

        service = setup.service

        assert set(service.services.keys()) == {"expert", "lyceen", "parcoursup"}

        expert_lyceen_expected_targets = {"fl1", "fl2", "fl3"}
        expert_lyceen_expected_features = {
            "interests:dom1",
            "interests:dom2",
            "formations_favorites:fl1",
            "formations_favorites:fl2",
            "formations_favorites:fl3",
            "specialites:sp1",
        }

        for name in ("expert", "lyceen"):
            engine = service.services[name]
            assert set(engine.matrix.columns) == expert_lyceen_expected_targets
            assert set(engine.matrix.index) == expert_lyceen_expected_features | {"bias"}
            assert set(engine.explanation_matrix.columns) == expert_lyceen_expected_targets
            assert set(engine.explanation_matrix.index) == expert_lyceen_expected_features

        parcoursup_expected_targets = {"fl1", "fl2", "fl3"}
        parcoursup_expected_features = {
            "formations_favorites:fl1",
            "formations_favorites:fl2",
            "formations_favorites:fl3",
        }

        parcoursup_engine = service.services["parcoursup"]
        assert set(parcoursup_engine.matrix.columns) == parcoursup_expected_targets
        assert set(parcoursup_engine.matrix.index) == parcoursup_expected_features | {"bias"}
        assert set(parcoursup_engine.explanation_matrix.columns) == parcoursup_expected_targets
        assert set(parcoursup_engine.explanation_matrix.index) == parcoursup_expected_features
