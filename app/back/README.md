# MonProjetSup Backend
Ce dossier contient le code nécessaire à toute l'api de l'application MonProjetSup. 
Elle utilise les technologies Kotlin/Spring Boot.

<!-- TOC -->
* [MonProjetSup Backend](#monprojetsup-backend)
  * [Comment lancer le projet](#comment-lancer-le-projet)
    * [Pré-requis](#pré-requis)
    * [Variables d'env](#variables-denv)
    * [Lancer la BDD](#lancer-la-bdd)
    * [Lancer le serveur](#lancer-le-serveur)
  * [Comment ajouter une variable d'env](#comment-ajouter-une-variable-denv)
  * [Le linter](#le-linter)
  * [La BDD](#la-bdd)
    * [Les migrations de BDD](#les-migrations-de-bdd)
    * [Le schéma de la BDD](#le-schéma-de-la-bdd)
    * [BDD dans un état incohérent](#bdd-dans-un-état-incohérent)
  * [Architecture du projet](#architecture-du-projet)
    * [Couche Application](#couche-application)
      * [Les controllers](#les-controllers)
      * [Les DTOs](#les-dtos)
      * [Hateoas](#hateoas)
    * [Couche Use Case](#couche-use-case)
      * [Les services](#les-services)
    * [Couche Infrastructure](#couche-infrastructure)
      * [Les repositories](#les-repositories)
      * [Les entités](#les-entités)
      * [Les clients HTTP](#les-clients-http)
        * [Client Suggestions`](#client-suggestions)
        * [Client Parcoursup](#client-parcoursup)
    * [Couche Domaine](#couche-domaine)
      * [Les entity](#les-entity)
      * [Les ports](#les-ports)
  * [Les exceptions](#les-exceptions)
  * [Les logs](#les-logs)
  * [L'authentification](#lauthentification)
  * [Les tests](#les-tests)
    * [Controller](#controller)
    * [Repository](#repository)
<!-- TOC -->

## Comment lancer le projet

### Pré-requis
- Java 19
- Kotlin
- Docker
- Maven

### Variables d'env
- Avant de pouvoir démarrer l'application il est nécessaire de créer le fichier `secrets.properties` situé au même niveau que `application.properties`. Le fait de redéfinir certaines variables dans ce fichier écrasera les valeurs par défaut de ces variables.
- Vous pouvez copier l'application.properties et le renommer en secrets.properties puis demander à un membre du projet les variables à renseigner.
- Pour la variable `springdoc.swagger-ui.oauth.client-secret` vous pouvez récupérer le secret à cette URL http://localhost:5003/admin/master/console/#/avenirs/clients/99b97c1a-5f00-4304-9a17-9f8c9fa3974d/credentials (si vous ne savez pas lancer/vous connecter au keycloak -> README.md à la racine de ce repo)

### Lancer la BDD

- Lancez la base de données avec `docker-compose up -d` qui est à la racine du projet

### Lancer le serveur
- Lancez le serveur avec `mvn clean compile exec:java -Dexec.mainClass=fr.gouv.monprojetsup.MonProjetSupApplication`

NB : N'oubliez pas de lancer l'ETL pour peupler la BDD avec des données de test (cf [README.md](../etl/README.md)) ainsi que l'API Suggestions (cf [README.md](../suggestions/README.md)) pour pouvoir tester l'API.

## Comment ajouter une variable d'env

- En local, vous devez tout d'abord définir la variable dans `application.properties` avec une valeur par défaut qui doit pointer vers une variable d'environnement de la machine (c'est ce qui sera utilisé sur les environnements de déploiement)
- Ajoutez dans le fichier `secrets.properties` la valeur pour votre environnement local
- Pour le déploiement, vous devez renseigner cette variable et sa valeur dans le Secret Manager Scaleway pour chacun des environnements.

## Le linter
Le linter installé est [Ktlint](https://pinterest.github.io/ktlint/0.49.1/install/integrations/)
Pour le lancer, il faut se placer dans le dossier `app/back` et lancer la commande

```mvn clean && mvn ktlint:format && mvn ktlint:check```

## La BDD

La BDD est une PostgreSQL. On utilise Docker pour la lancer en local.

### Les migrations de BDD

On utilise l'outil [Flyway](https://documentation.red-gate.com/flyway) pour migrer nos bases de données.

Il existe une CLI, mais le plus simple pour effectuer une migration reste de lancer tout simplement l'application qui effectuera elle-même les migrations.

Les migrations sont répercutées dans la BDD `flyway_schema_history`. On peut s'amuser en LOCAL à les supprimer à la main lors des conflits entre ≠ branches.

Pour effectuer une migration, il suffit d'ajouter un script sql dans le dossier `app/back/src/main/resources/db/migration`.
La nomenclature à respecter est VX_Y__nom_en_miniscule_decrivant_la_migration où X correspond à une version majeure et Y une version mineure.

Dans le cas de rebase, il faut penser à augmenter ce chiffre Y pour éviter les conflits avec de potentiels scripts déjà mergés par d'autres personnes.

Ce dossier est "partagé" avec le dossier `app/etl` pour éviter les problèmes de versionnement de la BDD. On utilise une redirection de dossier pour cela.

### Le schéma de la BDD

Le schéma de la BDD est décrit dans le fichier à la racine du projet.

<img alt="schéma_bdd_MPS.png" src="../../doc/sch%C3%A9ma_bdd_MPS.png" title="Schéma de la base de donnée"/>

Pour le nommage des tables, on utilise le format suivant : 
- `ref_nom_de_la_table` si la table est alimentée par l'ETL 
- `sugg_nom_de_la_table` si la table est utilisée par l'API Suggestions 
- `nom_de_la_table` si la table est alimentée et utilisée par le back.

La table `parametre` est utilisée pour stocker les paramètres de l'application. Elle est partagée par les 2 applications.

### BDD dans un état incohérent

Dans le cas où la BDD est dans un état incohérent (exemple : passage d'une branche de test à une autre) et qu'il est impossible de facilement rollback la migration, il est possible de la réinitialiser en : 
- Supprimant toutes les tables de la BDD
- Mettant en pause le container Docker de la BDD
- Supprimer le dossier `app/db`
- Relancer le container Docker de la BDD
- Relancer l'ETL (cf [README.md](../etl/README.md) à la racine du projet)

## Architecture du projet

:exclamation: Le projet est codé en français. Les noms des classes, des méthodes, des variables, etc. sont en français. Les accents ne sont cependant pas utilisés. :exclamation:

Sous le dossier [monprojetsup](src/main/fr/gouv/monprojetsup), on retrouve les dossiers métier comme `eleve`, `formation`, `authentification`, etc.
Chaque dossier est organisé de la même manière, selon une clean architecture, avec les exceptions des dossiers `commun`et `configuration`.

La clean architecture se base sur la séparation des couches (cf https://blog.scalablebackend.com/understand-the-theory-behind-clean-architecture). 

La nôtre se mixe avec une architecture héxagonale (cf https://blog.octo.com/architecture-hexagonale-trois-principes-et-un-exemple-dimplementation) où l'on sépare la dernière couche entre l'infrastructure qui comporte les repositories et les clients et la couche application qui contient les controllers.

### Couche Application

#### Les controllers

Ce sont les points d'entrée de l'API. Ils sont responsables de la gestion des requêtes HTTP et de la réponse HTTP. On les documente avec Swagger.
Ils ont peu de logiques, la transformation des données du domaine vers les DTO se fait dans la construction DTO.
Ils appellent les services pour effectuer les actions métier.

#### Les DTOs

Ces data class ne sont utilisés que dans la couche application. 
Elles gèrent la transformation des données du domaine vers les données de la couche présentation.

#### Hateoas

Des hateoas ont été ajoutés sur les appels de type GET pour les formations et les métiers afin de paginer les résultats.
Actuellement, la pagination n'est pas utilisée dans le front.

### Couche Use Case

#### Les services

Ils contiennent la logique métier de l'application. 
Ils font appel aux repositories pour accéder aux données ainsi qu'aux clients pour appeler d'autres services comme les services Suggestion ou Parcoursup.

### Couche Infrastructure

#### Les repositories

Ils contiennent la logique d'accès aux données. Ils sont responsables de la communication avec la base de données.
Chaque Repository implémente une interface qui se trouve dans la couche Domaine.
Ils regroupent un ou plusieurs JPARepository.

Voici les 2 manières de faire une requête en BDD :

- Soit en utilisant les méthodes de base de Spring Data JPA (https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods)
- Soit par une native query pour des cas complexes comme la recherche

Pour éviter le problème des N+1 appels (https://vladmihalcea.com/n-plus-1-query-problem/), nous n'utilisons plus `@ManyToOne` ou `@OneToMany`. 
Nous utilisons des queries pour récupérer les données. On peut soit utiliser l'`entityManager` ou l'annotation `@Query`.

#### Les entités

Les entités représentent une table de la base de données. 
Il peut y avoir plusieurs entités pour la même table, elles sont créées selon les besoins des requêtes.

#### Les clients HTTP

##### Client Suggestions`

Le client Suggestions ne comporte pas d'authentification. Il est utilisé pour récupérer des données de l'API Suggestions comme les formations suggérées pour un élève ou les explications de la suggestion.

##### Client Parcoursup

Le projet a 2 clients Parcoursup :

- Le **1er** permet de récupérer l'id d'un élève. Pour cela le front initie une connexion en OAuth2 avec Parcoursup et récupère un code. Ce code est envoyé avec le codeVerifier associé, au back qui appelle à son tour Parcoursup pour récupérer le token. Le flow est coupé entre le back et le front afin de sécuriser la récupération du JWT.

- Le **2nd** permet de se connecter à l'API des favoris de Parcoursup. L'authentification est une authentification serveur à serveur avec un client id et un client secret. Il permet de récupérer les favoris Parcoursup d'un élève.  

### Couche Domaine

#### Les entity

Les entity sont les objets métiers du projet. Elles sont généralement créées par les couches infrastructure et application et utilisées au sein des services. 

#### Les ports

Les ports sont hérités de l'architecture héxagonale. Ils permettent d'interfacer la couche infrastructure (Repository et Client).

## Les exceptions

Les erreurs sont gérées par le `ApplicationControllerAdvice` qui intercepte les exceptions et les transforme en réponse HTTP.
Les erreurs sont gérées par héritage de la classe mère `MonProjetSupExceptions`. 
Chaque erreur a pour parent une classe mère par type d'erreur (400, 404, 500, etc). On les nomme MonProjetSupXXXXError avec XXXX le type.

<u>Exemple pour la 404 Not Found</u> : `MonProjetSupNotFoundError`.
Toutes ces classes héritent de `MonProjetSupExceptions`qui est composé de 2 paramètres :
- `code` : le type de l'erreur (ex : FORMATION_NOT_FOUND) qui est une string fixe (ne dépendant pas de paramètres) en casse SCREAMING_SNAKE_CASE. Il permet de regrouper les erreurs au niveau des logs.
- `message` : le message de l'erreur (ex : "La formation fl0007 n'existe pas") qui est une explication plus précise de l'erreur avec potentiellement des paramètres comme des ids.

> Raison : cela permet de centraliser la gestion des erreurs et de les rendre plus facilement identifiables.

On utilise [le standard RFC 7807](https://datatracker.ietf.org/doc/html/rfc7807) soit la classe `ProblemDetails` de Spring. 
On lui fixe les paramètres génériques `title` et `detail` avec les paramètres `code` et `message`.

> Raison : cela permet de standardiser les erreurs

<b><u>Bonne pratique</u></b> : logger le message d'erreur grâce au `MonProjetSupLogger`.

## Les logs

Les logs sont gérées dans la classe `MonProjetSupLogger`.
Les 3 niveaux sont implémentés : 
- `INFO` pour les logs informatifs
- `WARN` pour les logs d'avertissement correspondant à un état non bloquant mais qui ne devrait pas arriver
- `ERROR` pour les logs d'erreur correspondant à un état bloquant

Une fonction `logReponse` permet de logguer les retours de l'API. Elle loggue le code de retour, la méthode ainsi que l'url.
Toutes ces informations sont stockées dans un objet `reponse` avec le `StructuredArguments.kv`.
Cette fonction est appelée dans `LogguerReponsesFilter` qui permet de logguer chaque réponse de l'API. 

Une fonction `logException`  permet de logguer les exceptions. Elle est appelée dans le `ApplicationControllerAdvice` pour logguer les exceptions interceptées.

## L'authentification

L'authentification est gérée par Keycloak. Actuellement tous les endpoints sont protégés et nécessitent une authentification.

Les fichiers utiles pour l'authentification sont :
- `SecuriteConfiguration` qui permet de configurer les autorisations ainsi que les Cors
- `IdentificationFilter` qui permet d'identifier l'utilisateur. Actuellement l'application ne gère que des élèves. Il est prévue de gérer les enseignants / PsyEN / accompagnant d'éducation ainsi que les proviseurs. La gestion des droits se fera alors à cet endroit.
- `AuthentifieController` qui est une classe abstraite dont on peut faire hériter les controller. Elle comporte les fonctions permettant de récupérer l'utilisateur stocké dans le contexte.

## Les tests

Les tests sont réalisés avec JUnit 5 et Mockito. Les inner class sont utilisée pour séparer les tests par fonction / appel API.

### Controller

Il s'agit de test d'intégration, simulant des appels API. Les services sont mockés car ce ne sont pas des tests end-to-end.
Une classe `ControllerTest` doit être héritée, elle comporte le nécessaire pour tester avec l'authentification.
Les annotations `@ConnecteAvecUnEleve`, `@ConnecteAvecUnEnseignant`, `@ConnecteSansId` permettent de simuler une connexion avec un élève, un enseignant ou sans id et doivent être placées au-dessus de chaque test.

### Repository

Il s'agit de test d'intégration, permettant de tester avec une BDD. On utilise TestContainer pour lancer un container le temps des tests.
Les tests héritent de la classe `BDDRepositoryTest` qui comporte le nécessaire pour lancer la BDD ainsi que la fermer une fois les tests exécutés.
Pour remplir la BDD pour chaque test, on utilise l'annotation `@Sql`. Un fichier situé dans `test/resources` comportant tous les `INSERT` nécessaires aux tests est créee pour chaque Repository. 