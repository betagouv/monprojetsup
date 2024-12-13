# MonProjetSup Frontend
Ce dossier contient le code nécessaire à toute la partie connectée (app) de l'application MonProjetSup. Elle utilise les technologies Typescript/React/ViteJS.

<!-- TOC -->
* [MonProjetSup Frontend](#monprojetsup-frontend)
  * [Comment lancer le projet](#comment-lancer-le-projet)
    * [Deux façons de lancer l'application](#deux-façons-de-lancer-lapplication)
    * [Pré-requis](#pré-requis)
    * [Définir les variables d'env](#définir-les-variables-denv)
    * [Lancer le serveur](#lancer-le-serveur)
  * [Variables d'env](#variables-denv)
  * [Architecture du projet](#architecture-du-projet)
    * [Principales librairies utilisées](#principales-librairies-utilisées)
    * [Arborescence](#arborescence)
    * [Le dossier features](#le-dossier-features)
  * [Le linter](#le-linter)
  * [Les tests](#les-tests)
  * [Notes complémentaires](#notes-complémentaires)
<!-- TOC -->

## Comment lancer le projet

### Deux façons de lancer l'application 
- **Mode normal** : l'application front a besoin que le backend et le keycloak soient lancés. C'est le mode utilisé sur tous les environnements de déploiement et celui recommandé pour développer.
- **Mode test** : l'application front fonctionnera de manière complètement indépendante et ne fera aucun appel au backend et au keycloak (ils n'ont pas besoin d'être lancés). L'application utilisera des données "InMemory" et le session storage du navigateur pour stocker le profil de l'utilisateur. Pour démarrer dans ce mode, il suffit de mettre la variable d'environnement `VITE_TEST_MODE` à `true`.

### Pré-requis
- Node version 22
- Si vous lancez l'application en "mode normal" : avoir suivi le README.md à la racine du monorepo et avoir une base de données remplie, le serveur suggestions et le backend démarrés et opérationnels.

### Définir les variables d'env
- Avant de pouvoir démarrer l'application, il est nécessaire de dupliquer le fichier `.env.example` à la racine et de le renommer en `.env.local`.
- Vous n'avez par défaut aucune modification à effectuer aux variables obligatoires autre que de récupérer la valeur de `VITE_KEYCLOAK_CLIENT_SECRET` à cette URL http://localhost:5003/admin/master/console/#/avenirs/clients/99b97c1a-5f00-4304-9a17-9f8c9fa3974d/credentials (si vous ne savez pas lancer/vous connecter au keycloak -> README.md à la racine de ce repo).

### Lancer le serveur
- Installez les dépendances avec `npm install`.
- Lancez le serveur avec `npm run dev`.

## Variables d'env
- Le fichier `.env.example` sert de référence pour la documentation de l'usage des variables.
- Le fichier `.env.ci` est utilisé par notre CI GitHub afin de trouver les variables d'env. Attention à ne pas y mettre d'infos sensibles. Si besoin, vous pouvez utiliser les secrets de GitHub.
- Il existe des variables obligatoires et facultatives.

### Ajouter une variable
- En local, vous devez définir la variable à plusieurs endroits : `.env.example`, `.env.local`, `.env.ci`.
- Dans le fichier `docker/Dockerfile`, vous devez déclarer son existence.
- Sur le GitLab Onisep, vous devez modifier le projet `monprojetsup_ci` et plus spécifiquement le fichier `build_application.yml` afin d'indiquer la valeur pour chacun des environnements.

### Remarques spécifiques pour certaines variables
- `VITE_KEYCLOAK_CLIENT_SECRET` Vous trouverez cela sûrement étrange (je l'espère) de définir un secret côté frontend et qui sera donc disponible publiquement. Mais dans notre cas d'utilisation, il n'y a pas de risque particulier associé (je ne rentre pas en détail dans le fonctionnement de keycloak et OIDC). Nous nous plions simplement à la configuration définie par Avenir(s).
- `VITE_MATOMO_URL` Cette variable est obligatoire de par son usage dans le fichier index.html pour les CSP (son absence rendrait les CSP invalides donc ignorés). Sur les environnements ne nécessitant pas de tracking analytics, vous pouvez la remplir avec n'importe quelle URL valide ou mieux, laisser celle par défaut dans le .env.example :)
- `VITE_MATOMO_SITE_ID` Vous pourriez constater des messages d'alerte en console concernant l'absence de cette variable. Aucun souci à se faire, c'est voulu qu'elle soit absente sur les environnements ne nécessitant pas de tracking analytics.

## Architecture du projet
🇫🇷 Le projet est codé en français au maximum (même si quelques anglicismes ou termes non traduits ont pu nous échapper). Les noms des classes, des méthodes, des variables, etc. sont en français. Les accents sont conservés. 

### Principales librairies utilisées
Pour construire ce projet, nous nous sommes appuyés sur des librairies "standards" qui sont au cœur de l'application. En voici une liste non exhaustive avec leur principal usage : 
- `Tanstack router`, gestion des pages, des URLs, et du routing général de l'application.
- `Tanstack query`, gestion des appels réseaux (ou non) et de leur mise en cache.
- `React DSFR`, utilisation de certains composants du DSFR sur étagère. 
- `Radix UI Primitives`, sous-couche accessibilité de certains composants.
- `Oidc-client-ts`, gestion de l'authentification et des tokens JWT.
- `React Hook Form`, gestion des formulaires.
- `Zod`, validation des formulaires.
- `Zustand`, création de stores. 
- `Tailwind`, pour tout ce qui touche aux styles.

### Arborescence
L'intégralité du code source se trouve dans le dossier `src`. 
Voici une explication basique de l'arborescence de dossier :
- `assets` c'est ici que sont stockés les SVG utilisés dans le projet (hormis les images de background CSS qui sont stockées sous `app/front/public`).
- `components` il s'agit de composants utilisés à plusieurs reprises dans l'application (composants mutualisés) ou composants de layout.
- `configuration` comme son nom l'indique, contient la configuration de certaines librairies mais également la gestion des clés de traductions, des variables d'environnement, des constantes et la déclaration des dépendances.
- `routes` il s'agit du répertoire utilisé par la librairie `Tanstack Router` afin de déterminer quelles URLs existent dans notre application et d'orienter vers les composants correspondants.
- `services` contient des modules pour la gestion des appels HTTP, des erreurs, des logs, et de l'analytics.
- `style` contient un unique fichier qui permet de charger Tailwind et de surcharger quelques propriétés CSS du DSFR.
- `tests-e2e` comme son nom l'indique, contient les fichiers de tests End To End (`Playwright`).
- `types` centralise quelques types Typescript utilisés dans toute l'application.
- `utils` fonctions utilitaires qui ne sont pas forcément spécifiques à notre projet. 
- `features` c'est sûrement l'endroit où vous passerez le plus de temps, parlons-en en détail.

### Le dossier `features`
Ce dossier contient les grandes fonctionnalités (features) de notre application. Chaque dossier suit une séparation en couches fortement inspirée de la `Clean Architecture` mais allégée et adaptée. 

Voici l'arborescence que vous trouverez dans chacune des fonctionnalités : 
- `domain` il s'agit de l'endroit où l'on définit l'objet ou les objets métiers que nous allons manipuler dans notre application. 
- `infrastructure/gateway` gère les interactions avec des services ou des systèmes externes, dans notre cas des API Http, du session storage ou bien de la gestion en mémoire.
- `ui` dans ce dossier se trouve la partie React et donc tous les composants liés à cette fonctionnalité.
- `usecase` contient les cas d'utilisation de l'application, qui représentent les actions métier principales que le système doit réaliser pour répondre aux besoins des utilisateurs ou des processus. Ces cas d'utilisation orchestrent les interactions entre les différentes couches de l'application (domaine, gateways) tout en restant indépendants des détails techniques.

## Le linter
Le projet utilise Eslint avec une configuration assez stricte. La CI se chargera de vérifier que le linter passe bien pour valider votre code. Je vous conseille donc en amont de lancer le linter de votre côté avant de push. 
- Lancer le linter : `npm run lint`
- Lancer le linter avec fixes automatiques si possible : `npm run lint:fix`

## Les tests
Le projet utilise `Vitest` (tests unitaires), `React Testing Library` (tests de composants/intégration) et principalement `Playwright` (tests End-to-End) afin d'assurer une couverture de test la plus pragmatique possible. 

L'approche sur ce projet a consisté à inverser la pyramide de test habituelle et à se concentrer principalement sur des tests E2E. De par leur principe, ces tests ne prétendent pas être exhaustifs mais devraient couvrir les cas d'usages les plus courants pour vous permettre d'effectuer des modifications avec un filet de sécurité minimum (cas passant). Je vous encourage fortement à les tenir à jour et à les compléter à la moindre régression constatée. 
- Lancer les tests unitaires / intégration : `npm run test`
- Lancer les tests End-to-End : `npx playwright test`



## Notes complémentaires
### Mettre à jour les types suite à des changements dans l'api back
Il est de la responsabilité de l'équipe back de ne pas introduire de breaking change dans l'api et de ce fait cette partie de documentation se trouve également dans le readme du back avec recommandation de lancer cette commande à chaque changement dans l'api. Cependant, si vous avez besoin de la lancer vous-même, voici la procédure.
- Assurez-vous d'avoir lancé le serveur back.
- Mettez-vous à la racine du projet front (app/front).
- Exécutez la commande `npx openapi-typescript http://localhost:5002/v3/api-docs -o ./src/types/api-mps.d.ts`.

### Mise en cache
L'application s'appuie grandement sur la mise en cache de tous les appels à l'aide de `Tanstack Query`. Il convient donc de bien avoir ça en tête lors du développement et de penser à invalider le cache là où c'est nécessaire. 

> There are only two hard things in Computer Science: cache invalidation and naming things. - Phil Karlton
