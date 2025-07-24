# ML Suggestions2 Endpoint

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
|`DB_SUGGESTIONS2_REF_EXPERT`| Nom de la table contenant les données de référence experts | :white_check_mark: Oui, défault: `profil_reference`|
|`DB_SUGGESTIONS2_REF_LYCEEN`| Nom de la table contenant les données de référence lycéens | :white_check_mark: Oui, défault: `profil_eleve`|


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