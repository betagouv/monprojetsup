# MonProjetSup Etl
Ce dossier contient le code nécessaire à l'alimentation des données de référence dans la BDD de MonProjetSup. 
Elle utilise les technologies Kotlin/Java/Spring Boot.

## Les migrations de BDD

On utilise l'outil [Flyway](https://documentation.red-gate.com/flyway) pour migrer nos bases de données.

Il existe une CLI, mais le plus simple pour effectuer une migration reste de lancer tout simplement l'application qui effectuera elle-même les migrations.

Les migrations sont répercutées dans la BDD `flyway_schema_history`. On peut s'amuser en LOCAL à les supprimer à la main lors des conflits entre ≠ branches.

Pour effectuer une migration, il suffit d'ajouter un script sql dans le dossier app/back/src/main/resources/db/migration.
La nomenclature à respecter est VX_Y__nom_en_miniscule_decrivant_la_migration où X correspond à une version majeure et Y une version mineure.

Dans le cas de rebase, il faut penser à augmenter ce chiffre Y pour éviter les conflits avec de potentiels scripts déjà mergés par d'autres personnes.  

## Comment lancer le projet
### Variables d'env
- Avant de pouvoir démarrer l'application il est nécessaire de créer le fichier `secrets.properties` situé au même niveau que `application.properties` dans le dossier `app/etl/etl-updatedb/src/main/resources`. Le fait de redéfinir certaines variables dans ce fichier écrasera les valeurs par défaut de ces variables.
- Voici les valeurs par défaut que vous pouvez créer
```
server.port=5002
spring.datasource.url=jdbc:postgresql://localhost:5431/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres

flyway.url=jdbc:postgresql://localhost:5431/postgres
flyway.user=postgres
flyway.password=postgres

dataRootDirectory=/data/mps/
```
La dernière valeur doit pointer sur un dossier contenant les fichiers de référence.


### Lancer le serveur
- Assurez-vous de disposer de java en version >= 19
- Depuis le dossier `app/etl/etl-updatedb` lancez le serveur avec ```mvn clean compile exec:java -Dexec.mainClass=fr.gouv.monprojetsup.data.etl.UpdateDbRunnerKt```

