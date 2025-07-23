# README

Cet outil permet d'entraîner les paramètres beta utilisés pour agréger plusieurs scores individuels en un score unique. La formule utilisée pour l’agrégation est :

```math
\text{aggregated\_score} = \prod_{i} (\beta_i \cdot (1 - s_i) + s_i)
```

où :
- $s_i$ sont les scores individuels
- $\beta_i$ sont les poids à apprendre.

## Installation

Pour installer les librairies nécessaires, nous conseillons de créer un environnement virtuel à l'aide des commandes suivantes :

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```


## Format du jeu de données

Le jeu de données d’entraînement est un fichier `.json` contenant une liste de triplets :

```json
[
  [[0.1, 0.5, ...], [0.2, 0.6, ...], 1.0],
  [[0.3, 0.4, ...], [0.1, 0.2, ...], 0.0],
  ...
]
```

Chaque triplet est composé de :
- Deux listes de scores individuels correspond à deux formations (appelons-les `F1` et `F2`).
- Une valeur de préférence : `1.0` si la formation `F1` est préférée à `F2`, `0.0` si `F2` est préférée à `F1`.

## Configuration

Les paramètres d'entraînement suivants peuvent être personnalisés dans le fichier `learning_betas.py` :

- `TRAINING_DATASET_FILE` : chemin vers le fichier de données d’entrée (format JSON).
- `OUTPUT_FILE` : chemin où est sauvegardé le fichier JSON contenant les résultats de l'apprentissage. Ce fichier contient les paramètres beta appris et la configuration utilisée pour l'entraînement.
- `SCORES` : liste des noms correspondant aux scores individuels, dans le même ordre que celui utilisé dans le jeu de données.
- `EPOCHS` : nombre d’époques d’entraînement.
- `LEARNING_RATE` : taux d’apprentissage utilisé par l'optimiseur.
- `BATCH_SIZE` : taille des lots d'entraînement.
- `FIXED_BETA` : dictionnaire des paramètres beta à figer pendant l’entraînement.
- `DEFAULT_INIT_BETA` : valeur initiale des beta pour les paramètres non présents dans `FIXED_BETA`.


## Exécution du script

Pour lancer l'entraînement, exécutez simplement le script avec Python

```bash
python learning_betas.py
```