# MonProjetSup Backend
Ce dossier contient le code nécessaire à toute l'api de l'application MonProjetSup. 
Elle utilise les technologies Kotlin/Spring Boot.

## Les migrations de BDD

On utilise l'outil [Flyway](https://documentation.red-gate.com/flyway) pour migrer nos bases de données.

Il existe une CLI, mais le plus simple pour effectuer une migration reste de lancer tout simplement l'application qui effectuera elle-même les migrations.

Les migrations sont répercutées dans la BDD `flyway_schema_history`. On peut s'amuser en LOCAL à les supprimer à la main lors des conflits entre ≠ branches.

Pour effectuer une migration, il suffit d'ajouter un script sql dans le dossier app/back/src/main/resources/db/migration.
La nomenclature à respecter est VX_Y__nom_en_miniscule_decrivant_la_migration où X correspond à une version majeure et Y une version mineure.

Dans le cas de rebase, il faut penser à augmenter ce chiffre Y pour éviter les conflits avec de potentiels scripts déjà mergés par d'autres personnes.  

## Comment ajouter une variables d'env
- En local, vous devez tout d'abord définir la variable dans `application.properties` avec une valeur par défaut qui doit pointer vers une variable d'environnement de la machine (c'est ce qui sera utilisé sur les environnements de déploiement)
- Dans le fichier `docker-compose.dev.yml` à la racine du repo, trouvez les variables associées à la machine `appback` et rajoutez votre variable d'env en lui associant sa valeur. **_Attention à ne pas saisir de données sensibles ce fichier est versionné_.**
- Si vous souhaitez utiliser une donnée sensible ou que vous ne souhaitez pas utiliser docker, vous pouvez créer et/ou éditer un fichier `secrets.properties` situé au même niveau que `application.properties`. Le fait de redéfinir certaines variables dans ce fichier écrasera les valeurs par défaut de ces variables.
- Pour le déploiement, vous devez les renseigner dans le Secret Manager Scaleway pour chacun des environnements.