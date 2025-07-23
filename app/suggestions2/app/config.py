import logging
from app.domain.models.config import ProfileConfig

VERSION = "0.3.0"

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
