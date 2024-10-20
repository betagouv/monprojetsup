# MonProjetSup Suggestions
Ce dossier contient le code du serveur de suggestions de MonProjetSup.
Elle utilise les technologies Kotlin/Java/Spring Boot.


## Démarrer le serveur de suggestions

### Installation de la dépendance etl-common
- La compilation du serveur de suggestion dépend du module ```etl-common``` (qui fournit la définition des entities de la bdd)
- Placez vous dans le dossier `app/etl/`
- Exécutez la commande ```mvn clean compile install -DskipTests=true``` pour installer le module dans votre repository local

### Paramétrage de l'accès à la BDD et du port du service
- Avant de pouvoir démarrer l'application il est nécessaire de créer un fichier `secrets.properties` situé au même niveau que `application.properties` dans le dossier `app/suggestions/suggestions-server/src/main/resources` qui définira la port exposé du service ainsi que la connection à la bdd.
- Voici un exemple de valeurs de ce fichier
```
server.port=8004
spring.datasource.url=jdbc:postgresql://localhost:5431/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
```

La Bdd doit avoir été initialisée avec l'etl.

### Lancer le serveur
- Assurez-vous de disposer de java en version 19
- Placez vous dans le dossier `app/suggestions/` 
- Tapez ```mvn -DskipTests=true clean compile install```
- Lancer le serveur avec ```mvn clean compile -f./suggestions-server exec:java -DskipTests=true -Dexec.mainClass=fr.gouv.monprojetsup.suggestions.server.ApplicationSuggestionsKt```

