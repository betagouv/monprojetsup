# Mon Projet Sup

Ce repository est un mono-repo qui contient plusieurs applications nécessaires au fonctionnement de Mon Projet Sup

## Applications contenu dans le repo
- **v1** - l'application historique (ce dossier est amené à disparaitre)
- **etl** - Permet de transformer les fichiers référentiels en JSON afin de les écrire dans une base Redis utilisée par toutes les applications
- **suggestions** - API permettant de générer des suggestions de formations
- **app** - coeur de l'application Mon Projet Sup
  - front - Frontend de tout le projet 
  - back - API avec laquelle communique le frontend

## Prérequis
Java, Kotlin, Docker

## Comment démarrer

### Lancer la BDD

- Lancer Docker
- À la racine du projet lancer la commande :
```bash
 docker compose -f docker-compose.dev.yml up appdb 
```

### Lancer l'API
À la racine du projet lancer la commande :
```bash
docker compose -f docker-compose.dev.yml up appback
```

### Lancer l'API suggestion pour certains endpoint 
cf [v1/back/README.md](v1/back/README.md)

## Les migrations de BDD

On utilise l'outil [Flyway](https://documentation.red-gate.com/flyway) pour migrer nos bases de données.

Il existe une CLI, mais le plus simple pour effectuer une migration reste de lancer tout simplement l'application qui effectuera elle-même les migrations.

Les migrations sont répercutées dans la BDD `flyway_schema_history`. On peut s'amuser en LOCAL à les supprimer à la main lors des conflits entre ≠ branches.

Pour effectuer une migration, il suffit d'ajouter un script sql dans le dossier app/back/src/main/resources/db/migration.
La nomenclature à respecter est VX_Y__nom_en_miniscule_decrivant_la_migration où X correspond à une version majeure et Y une version mineure.

Dans le cas de rebase, il faut penser à augmenter ce chiffre Y pour éviter les conflits avec de potentiels scripts déjà mergés par d'autres personnes.  