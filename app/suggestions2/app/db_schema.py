"""
Configuration des noms de tables, colonnes, et champs JSON pour la base de données.
"""


class DbTables:
    """Noms par défaut des tables de profils de référence."""

    REF_EXPERT = "profil_reference_expert"
    REF_LYCEEN = "profil_reference_lyceen"


class DbColumns:
    """Noms des colonnes dans la table des profils étudiants."""

    ID = "id"
    SITUATION = "situation"
    CLASSE = "classe"
    ID_BACCALAUREAT = "id_baccalaureat"
    DUREE_ETUDES_PREVUE = "duree_etudes_prevue"
    ALTERNANCE = "alternance"
    SPECIALITES = "specialites"
    DOMAINES = "domaines"
    CENTRES_INTERETS = "centres_interets"
    METIERS_FAVORIS = "metiers_favoris"
    CORBEILLE_FORMATIONS = "corbeille_formations"
    COMMUNES_FAVORITES = "communes_favorites"
    FORMATIONS_FAVORITES = "formations_favorites"
    VOEUX_FAVORIS = "voeux_favoris"


class JsonNestedFields:
    """
    Noms des champs à extraire des objets JSON stockés dans certaines colonnes.

    Certaines colonnes (formations_favorites, communes_favorites, voeux_favoris)
    contiennent des tableaux d'objets JSON. Ces constantes définissent les noms
    des champs à extraire de chaque objet lors du parsing.

    Exemples:
    - formations_favorites: [{"idFormation": "fl210", ...}, ...]
    - communes_favorites: [{"codeInsee": "33514", ...}, ...]
    - voeux_favoris: [{"idVoeu": "v123", ...}, ...]
    """

    FORMATION_ID = "idFormation"
    VOEU_ID = "idVoeu"
    COMMUNE_CODE_INSEE = "codeInsee"
