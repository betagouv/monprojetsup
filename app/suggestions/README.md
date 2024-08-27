# MonProjetSup Suggestions
Ce dossier contient le code du serveur de suggestions de MonProjetSup.
Elle utilise les technologies Kotlin/Java/Spring Boot.


## Comment lancer le projet
### Variables d'env
- Avant de pouvoir démarrer l'application il est nécessaire de créer le fichier `secrets.properties` situé au même niveau que `application.properties` dans le dossier `app/suggestions/suggestions-server/src/main/resources`. Le fait de redéfinir certaines variables dans ce fichier écrasera les valeurs par défaut de ces variables.
- Voici les valeurs par défaut que vous pouvez créer
```
server.port=8004
spring.datasource.url=jdbc:postgresql://localhost:5431/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
```
La dernière valeur doit pointer sur un dossier contenant les fichiers de référence.


### Lancer le serveur
- Assurez-vous de disposer de java en version 17
- Depuis le dossier `app/etl/etl-updatedb` lancez le serveur avec ```mvn clean compile exec:java -Dexec.mainClass=fr.gouv.monprojetsup.data.etl.UpdateDbRunnerKt```

