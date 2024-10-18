# MonProjetSup Backend
Ce dossier contient le code nécessaire à toute l'api de l'application MonProjetSup. 
Elle utilise les technologies Kotlin/Spring Boot.

## Les migrations de BDD
Le linter installé est [Ktlint](https://pinterest.github.io/ktlint/0.49.1/install/integrations/)
Pour le lancer, il faut se placer dans le dossier `app/back` et lancer la commande  

```mvn clean && mvn ktlint:format && mvn ktlint:check```

## Les migrations de BDD

On utilise l'outil [Flyway](https://documentation.red-gate.com/flyway) pour migrer nos bases de données.

Il existe une CLI, mais le plus simple pour effectuer une migration reste de lancer tout simplement l'application qui effectuera elle-même les migrations.

Les migrations sont répercutées dans la BDD `flyway_schema_history`. On peut s'amuser en LOCAL à les supprimer à la main lors des conflits entre ≠ branches.

Pour effectuer une migration, il suffit d'ajouter un script sql dans le dossier app/back/src/main/resources/db/migration.
La nomenclature à respecter est VX_Y__nom_en_miniscule_decrivant_la_migration où X correspond à une version majeure et Y une version mineure.

Dans le cas de rebase, il faut penser à augmenter ce chiffre Y pour éviter les conflits avec de potentiels scripts déjà mergés par d'autres personnes.  

## Comment lancer le projet
### Variables d'env
- Avant de pouvoir démarrer l'application il est nécessaire de créer le fichier `secrets.properties` situé au même niveau que `application.properties`. Le fait de redéfinir certaines variables dans ce fichier écrasera les valeurs par défaut de ces variables.
- Voici les valeurs par défaut que vous pouvez créer
```
server.port=5002
spring.datasource.url=jdbc:postgresql://localhost:5431/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
suggestions.api.url=http://localhost:8004/api/1.2
cors.originPatterns=http://localhost:5001
spring.security.oauth2.client.registration.keycloak.client-id=mps-cli
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:5003/realms/avenirs
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:5003/realms/avenirs
springdoc.swagger-ui.oauth.client-id=mps-cli
springdoc.swagger-ui.oauth.client-secret=
parcoursup.api.favoris.client.id=
parcoursup.api.favoris.client.password=
parcoursup.api.favoris.url.token=
parcoursup.api.favoris.url=
```
- Pour la variable `springdoc.swagger-ui.oauth.client-secret` vous pouvez récupérer le secret à cette URL http://localhost:5003/admin/master/console/#/avenirs/clients/99b97c1a-5f00-4304-9a17-9f8c9fa3974d/credentials (si vous ne savez pas lancer/vous connecter au keycloak -> README.md à la racine de ce repo)

### Lancer le serveur
- Assurez-vous de disposer de java en version 17
- Lancez le serveur avec `mvn clean compile exec:java -Dexec.mainClass=fr.gouv.monprojetsup.MonProjetSupApplication`


## Comment ajouter une variables d'env
### Dont la valeur dépend de l'environnement
- En local, vous devez tout d'abord définir la variable dans `application.properties` avec une valeur par défaut qui doit pointer vers une variable d'environnement de la machine (c'est ce qui sera utilisé sur les environnements de déploiement)
- Ajoutez dans le fichier `secrets.properties` la valeur pour votre environnement local
- Pour le déploiement, vous devez renseigner cette variable et sa valeur dans le Secret Manager Scaleway pour chacun des environnements.

### Dont la valeur ne dépend pas de l'environnement
- En local, editez le fichier `application.properties` pour y ajouter la variable et sa valeur
- Pour le déploiement, vous devez renseigner cette variable et sa valeur dans le Secret Manager Scaleway pour chacun des environnements.