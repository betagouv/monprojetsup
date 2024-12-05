# MonProjetSup Etl
Ce dossier contient le code nécessaire à l'alimentation des données de référence dans la BDD de MonProjetSup. 
Elle utilise les technologies Kotlin/Java/Spring Boot.

<!-- TOC -->
* [MonProjetSup Etl](#monprojetsup-etl)
  * [Les migrations de BDD](#les-migrations-de-bdd)
  * [Mettre à jour les données de référence dans la bdd de MonProjetSup](#mettre-à-jour-les-données-de-référence-dans-la-bdd-de-monprojetsup)
    * [Configuration](#configuration)
    * [Effectuer la mise à jour de la bdd MPS](#effectuer-la-mise-à-jour-de-la-bdd-mps)
  * [Mettre à jour les données de référence Ideo Onisep](#mettre-à-jour-les-données-de-référence-ideo-onisep)
  * [Mettre à jour les données de référence Parcoursup](#mettre-à-jour-les-données-de-référence-parcoursup)
    * [Fichiers de configuration](#fichiers-de-configuration)
    * [Effectuer la mise à jour des fichiers Parcoursup](#effectuer-la-mise-à-jour-des-fichiers-parcoursup)
    * [Tests gourmands en mémoire](#tests-gourmands-en-mémoire)
<!-- TOC -->

## Les migrations de BDD

cf [README.md](../back/README.md) du backend **Les migrations de BDD**

## Mettre à jour les données de référence dans la bdd de MonProjetSup

### Configuration
- Il est nécessaire de configurer la connexion à la BDD MPS en créant un fichier `secrets.properties` situé au même niveau que `application.properties` dans le dossier `app/etl/etl-updatedb/src/main/resources`. Le fait de redéfinir certaines variables dans ce fichier écrasera les valeurs par défaut de ces variables.
- Voici les valeurs par défaut que vous pouvez créer
```
spring.datasource.url=jdbc:postgresql://localhost:5431/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres

flyway.url=jdbc:postgresql://localhost:5431/postgres
flyway.user=postgres
flyway.password=postgres

dataRootDirectory=/data/mps/
```
La dernière valeur doit pointer sur un dossier contenant les fichiers de référence.

### Effectuer la mise à jour de la bdd MPS
- Assurez-vous de disposer de java en version >= 19
- Placez-vous dans le dossier `app/etl/`
- Installez les librairies dans le repo local avec ```mvn -DskipTests=true clean compile install```
- Lancez la tâche de mise à jour avec ```mvn clean compile -fetl-updatedb/pom.xml exec:java -DskipTests=true -Dexec.mainClass=fr.gouv.monprojetsup.data.etl.UpdateMpsDbKt```

## Mettre à jour les données de référence Ideo Onisep

Assurez-vous de disposer de java en version >= 19
- La machine doit disposer d'une connexion à internet
- Placez-vous dans le dossier `app/etl/`
- configurez le paramètre `dataRootDirectory` dans un fichier `secrets.properties` situé au même niveau que `application.properties` dans le dossier `app/etl/etl-ideo/src/main/resources`.
- Lancez la tâche de mise à jour avec ```mvn clean compile -fetl-ideo/pom.xml exec:java -Dexec.mainClass=fr.gouv.monprojetsup.data.etl.UpdateIdeoDataKt```

## Mettre à jour les données de référence Parcoursup

### Fichiers de configuration
- Il est nécessaire de configurer la connexion à la BDD Parcoursup en créant un fichier `secrets.properties` situé au même niveau que `application.properties` dans le dossier `app/etl/etl-updatedb/src/main/resources`. Le fait de redéfinir certaines variables dans ce fichier écrasera les valeurs par défaut de ces variables.
- Voici un exemple de valeurs par défaut que vous pouvez créer
```
dataRootDirectory=./data
psup.url=jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=host.fr)(PORT=7777))(CONNECT_DATA=(SERVICE_NAME=name)))
psup.username=login
psup.password=password
```
### Effectuer la mise à jour des fichiers Parcoursup
Assurez-vous de disposer de java en version >= 19
- La machine doit disposer d'une connexion directe à la BDD de Parcoursup 
- Placez-vous dans le dossier `app/etl/`
- Lancez la tâche de mise à jour avec ```mvn clean compile -fetl-psup/pom.xml exec:java -Dexec.mainClass=fr.gouv.monprojetsup.data.etl.UpdatePsupDataKt```

### Tests gourmands en mémoire
Certains tests sont trop gourmands en ressources mémoire pour être exécuté sur une CI standard.
Dans le code les tests sont annotés avec `@Tag("resource-intensive-test")`.
Ces tests sont désactivés dans le profile maven "ci".

## Génération des données de référence MPS à partir des données IDEO, Onisep et Parcoursup

La base de l'indexation des données de référence de MonProjetSup est constituée de données provenant de 3 sources : IDEO, Onisep et Parcoursup.
Pour générer les données de référence de MonProjetSup à partir des données de ces 3 sources, il faut suivre les étapes suivantes :
- la base de l'indexation est le système de g_fl_cod de Parcoursup
- certains g_fl_cod sont regroupés en familles de g_fl_cod, ou g_fr_cod
- 
