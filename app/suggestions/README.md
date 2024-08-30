# MonProjetSup Suggestions
Ce dossier contient le code du serveur de suggestions de MonProjetSup.
Elle utilise les technologies Kotlin/Java/Spring Boot.


## Démarrer le serveur de suggestions

### Installation de la dépendance etl-common
- La compilation du serveur de suggestion dépend du module ```ètl-common``` (qui fournit la définition des entities de la bdd)
- Depuis le dossier `app/etl/etl-common` lancez la commande ```mvn clean compile install -DskipTests=true``` pour installer le module dans votre repository local

### Variables d'env
- Avant de pouvoir démarrer l'application il est nécessaire de créer un fichier `secrets.properties` situé au même niveau que `application.properties` dans le dossier `app/suggestions/suggestions-server/src/main/resources` qui définira la port exposé du service ainsi que la connection à la bdd.
- Voici un exemple de valeurs de ce fichier
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

