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

### Paramétrage de l'utilisation de suggestions2
- Pour utiliser le service de suggestions2, deux possibilités.
- La première consiste à modifier le fichier `secrets.properties` de `suggestions-server` en ajoutant la propriété suivante:
```properties
mps.suggestions2.url=http://localhost:5005
mps.suggestions2.enabled=true
```
La seconde à positionner deux variabls d'environnement
```environnement
SUGGESTIONS2_URL=http://localhost:5005
SUGGESTIONS2_ENABLED=true
```
Et bien sûr il faut lancer le service `suggestions2` pour cela consulter le Readme du service `suggestions2`.

### Paramétrage des explications détaillées
- Pour activer les explications détaillées, ajouter la propriété suivante dans le fichier `secrets.properties`:
```properties
mps.generateDetailedExplanations=true
```
ou bien positionner la variable d'environnement
```environnement
GENERATE_DETAILED_EXPLANATIONS=true
```


### Lancer le serveur
- Assurez-vous de disposer de java en version 19
- Placez vous dans le dossier `app/suggestions/` 
- Tapez ```mvn -DskipTests=true clean compile install```
- Lancer le serveur avec ```mvn clean compile -f./suggestions-server exec:java -DskipTests=true -Dexec.mainClass=fr.gouv.monprojetsup.suggestions.server.ApplicationSuggestionsKt```

### Modifier la configuration de l'algorithme de génération des suggestions personnalisées
La configuration des suggestions est stockée dans la table `sugg_config`.
Les modifications de cette table sont prises en compte en temps réel par le serveur de suggestions, à 1Hz.

Une autre possibilité est d'utiliser le service `SetSuggestionsConfigService`.
PAr défaut, ce service est désactivé,
car son usage doit être restreint aux configurations de développement.
Il est activable en ajoutant la propriété suivante dans le fichier `secrets.properties`:
```
mps.suggestions.dynamic_parameter_service.enabled=true
```
L'appel à ce service modifie également la configuration des suggestions stockées dans la table `sugg_config`.