import logging
from pathlib import Path

from app.domain.models.config import ProfileConfig

VERSION = "0.3.0"

# Default directory for storing precomputed models (models can be computed by app.tools.precompute_models)
DEFAULT_MODELS_DIR = Path(__file__).parent.parent / "models"

CONFIG = ProfileConfig(
    use_id_baccalaureat=True,
    use_duree_etudes_prevue=False,
    use_specialites=True,
    use_interests=True,
    use_metiers_favoris=True,
    use_corbeille_formations=True,
    use_communes_favorites=False,
    use_formations_favorites=True,
    targets={
        "formations_favorites",
    },
)


LOGGER = logging.getLogger("uvicorn.error.app")
LOGGER.setLevel("DEBUG")
