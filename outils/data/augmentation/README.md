# Outil de suggestions de nouveaux edges

`Augmentation` est un outil de proposition de nouveau liens formation-domaine d'intérêt ("edges") à partir de leurs descriptions, en calculant une proximité sémantique avec des LLMs.

Pour chaque domaine, le programme calcul la proximité avec chaque formation, et renvoie la liste des paires les plus pertinentes qui ne figurent pas déjà dans le système.

## Installation des dépendances

Pour installer les librairies requises par ce script, lancer la commande suivante:
```bash
pip install -r requirements.txt
```

## Configuration
Pour se connecter à la base de données, le script utilise des variables d'environnement spécifiées dans le fichier `.env`, avec le format suivant (voir aussi `.env.example`)
```bash
DB_PORT="1234"
DB_HOST="localhost"
DB_NAME="postgres"
DB_USERNAME="postgres_user"
DB_PASSWORD="password"
```

## Utilisation

```bash
python main.py [-o OUTPUT_FILE] [-c] [-n MAX_COUNT] [-k TOP_K]
```

Le script a les options suivantes:

- `-h` : affiche le message d'aide et termine l'execution. 
- `-n MAX_COUNT` : renvoie au maximum `MAX_COUNT` suggestions d'edges par domaine. (Défaut : 2)
- `-k TOP_K` : produire uniquement des suggestions d'edge edges entre un domaine et les formations dans le top `TOP_K` des meilleures formations pour ce domaine. (Défaut : 10)
- `-c` : utiliser la *cosine similarity* au lieu du produit scalaire pour le calcul de score.
- `-o OUTPUT_FILE` : Chemin du fichier où écrire les résultats. (Défaut : 'results.csv')
