# ML Suggestions2 Endpoint

## Fonctionnement

### Scores de suggestions

Suggestions2 calcule et renvoie trois scores pour chaque formation :

| Score | Source de données par défaut dans la DB | Description |
|:------|:------------------|:------------|
| **expert** | `profil_reference_expert` | Basé sur les profils de référence créés par les experts métier |
| **lyceen** | `profil_reference_lyceen` | Basé sur les profils des lycéens utilisant MonProjetSup |
| **parcoursup** | `sugg_paniers_voeux` | Basé sur les paniers de vœux Parcoursup |

#### Détails de fonctionnement du score Parcoursup

Ce score exploite les paniers de vœux issus des données Parcoursup.

Le principe est d'apprendre quelles formations sont fréquemment choisies ensemble par les candidats Parcoursup. Si un utilisateur s'intéresse à certaines formations, ce score suggère les formations qui étaient souvent présentes dans les mêmes paniers de vœux.

Il fonctionne de la manière suivante :

1. **Chargement des paniers de vœux** : La table `sugg_paniers_voeux` contient les vœux (du type `taXXX`) de candidats Parcoursup, regroupés par panier (un panier = les vœux d'un candidat).

2. **Conversion vœux -> formations** : Les vœux Parcoursup (`taXXX`) correspondent à des formations spécifiques dans des établissements précis. La table de jointure `ref_join_formation_voeu` permet de convertir ces vœux en formations génériques (`flXXX`) utilisées par MonProjetSup.

3. **Apprentissage des co-occurrences** : Chaque panier est ensuite traité comme un "profil" contenant un ensemble de formations favorites. A partir des ces profils, le modèle Naive Bayes (cf. la classe `NaiveBayesMatrix` du fichier `naive_bayes.py`) construit une matrice contenant les probabilités qu'une formation soit choisie sachant qu'une autre formation est présente dans le panier.

4. **Calcul du score** : Pour un utilisateur donné, le score `parcoursup`d'une formation est alors calculé en fonction de toutes les formations qu'il a déjà marquées comme favorites, et de la matrice précédemment construite. Plus une formation apparaît fréquemment avec les formations favorites de l'utilisateur, plus son score sera élevé.


## Comment lancer le endpoint suggestions2

### Paramétrer l'accès à la base de données

Le endpoint a besoin pour fonctionner d'un accès à une db contenant les données de référence de MPS.
La configuration de l'accès à la db se fait via les variables d'environnement, de deux manières possible :
- soit en assignant leur valeur dans l'environnement,
- soit en remplissant un fichier `.env` avec l'assignement des variables, voir le fichier `.env.example` pour un exemple.

Les variables d'environnement sont prioritaires sur le fichier.

Récapitulatif des variables d'environnement:
| Nom | Description | Optional ? |
|:----|:------------|:-------------:|
|`DB_SUGGESTIONS2_PORT`| Port à utiliser pour se connecter à l'hote de la BDD | :x: Non |
|`DB_SUGGESTIONS2_NAME`| Nom de la base dans laquelle récupérer les données | :x: Non |
|`DB_SUGGESTIONS2_USERNAME`| Nom d'utilisateur pour accéder à la BDD | :x: Non |
|`DB_SUGGESTIONS2_PASSWORD`| Mot de passe pour accéder à la BDD | :x: Non |
|`DB_SUGGESTIONS2_HOSTNAME`| Nom de l'hôte de la base de données | :white_check_mark: Oui, défault: `localhost`|
|`DB_SUGGESTIONS2_REF_EXPERT`| Nom de la table contenant les données de référence experts | :white_check_mark: Oui, défault: `profil_reference_expert`|
|`DB_SUGGESTIONS2_REF_LYCEEN`| Nom de la table contenant les données de référence lycéens | :white_check_mark: Oui, défault: `profil_reference_lyceen`|
|`DB_SUGGESTIONS2_PANIERS_VOEUX`| Nom de la table contenant les paniers de vœux | :white_check_mark: Oui, défault: `sugg_paniers_voeux`|
|`DB_SUGGESTIONS2_JOIN_FORMATION_VOEU`| Nom de la table de jointure formation-vœu | :white_check_mark: Oui, défault: `ref_join_formation_voeu`|


### Lancer l'application manuellement

L'application utilise [`uv`](https://docs.astral.sh/uv/) pour la gestion du projet.

#### Installer les dépendances 

```bash
uv sync
```

#### Execution de l'application

En mode production:
```bash
uv run fastapi run app/setup_fastapi.py --port 5445
```

#### Accéder au swagger

http://localhost:5445/docs

## Métriques

L'endpoint exporte des métriques de performance compatibles avec Prometheus à l'URL `http://localhost:5445/metrics`. (Remplacer `5445` par le port où l'endpoint est exposé.)

## Environnement de développement

### Installer les dépendances de dev

Voir ci-dessus pour créer le `.venv` et installer les dépendances.
Ensuite, 
```bash
uv sync --dev
```

### Lancer l'application en mode dev

En mode dev:
```bash
uv run fastapi dev app/setup_fastapi.py --port 5445
```

L'application se recharge automatiquement après modification des fichiers

### Executer les tests
```bash
uv sync --dev
uv run pytest
```

## Lancer l'application en mode docker

Build l'image:
```bash
docker build -t suggestions2 .
```

## Lancer le conteneur

Le conteneur doit avoir accès à la BDD pour charger les données.

### Lancer le conteneur pour une BDD accessible en local

Si la BDD est accessible en local, par exemple à travers un tunnel SSH, vous pouvez utiliser la méthode ci-dessous.

:warning: Ne pas utiliser en prod ! :warning:

Run:
```bash
docker run --env-file=.env --net=host suggestions2
```

Remplacez `.env` par le nom du fichier contenant les variables d'environnement.

### Cas général

Il faut placer le conteneur de `suggestions2` et celui de la BDD dans un même réseau, et assigner à la variable `DB_SUGGESTIONS2_HOSTNAME` le nom d'hôte de la BDD sur ce réseau.
Il faut aussi exposer le port 80 de `suggestions2` (potentiellement mappé à un autre port): l'API sera accessible via ce port.


### Noms des champs de la base de données

Pour voir ou modifier les noms des tables et champs utilisés dans la base de données par défaut, se référer au fichier `app/suggestions2/app/db_schema.py`.