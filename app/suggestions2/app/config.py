from enum import Enum

VERSION = "0.2.0"


class Variable(Enum):
    FormationsFavorites = 0
    CorbeilleFormations = 1
    Specialites = 2
    DomainesEtInterets = 3
    VoeuxParcoursup = 4
    Metiers = 5


"""
Defines which variables should be used for predicting the target (list for formations).
"""
BASKET_CONFIG = {
    "features": [
        Variable.FormationsFavorites,
        Variable.CorbeilleFormations,
        Variable.Specialites,
        Variable.DomainesEtInterets,
        # Variable.VoeuxParcoursup,
        Variable.Metiers,
    ],
}
