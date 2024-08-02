# MonProjetSup Frontend
Ce dossier contient le code nécessaire à toute la partie connectée (app) de l'application MonProjetSup. 
Elle utilise les technologies Typescript/React/ViteJS.

## Comment lancer le projet
### Variables d'env
- Avant de pouvoir démarrer l'application il est nécessaire de dupliquer le fichier `.env.example` à la racine et de le renommer en `.env.local`
- Vous n'avez par défaut aucune modification à effectuer autre que de récupérer la valeur de `VITE_KEYCLOAK_CLIENT_SECRET` à cette URL http://localhost:5003/admin/master/console/#/avenirs/clients/99b97c1a-5f00-4304-9a17-9f8c9fa3974d/credentials (si vous ne savez pas lancer/vous connecter au keycloak -> README.md à la racine de ce repo)

### Lancer le serveur
- Assurez-vous de disposer de la dernière version de NodeJS 22
- Installez les dépendances avec `npm install`
- Lancez le serveur avec `npm run dev`


## Comment lancer les tests et linters
- Linters: `npm run lint`
- Tests unitaires / intégration: `npm run test`
- Tests End-to-End: `npx playwright test`


## Comment ajouter une variables d'env
- En local, vous devez définir la variable à plusieurs endroits `.env.example`, `.env.local`, `.env.ci`
- Pour le déploiement vous devez demander à l'équipe en charge du gitlab Avenir(s) de vous rajouter la variable à leur script de CD pour tous les environnements (cf README.md à la racine de ce repo)

## Remarque sur les variables d'environnement existantes
### `VITE_KEYCLOAK_CLIENT_SECRET`
Vous trouverez cela surement étrange (je l'éspère) de définir un secret côté frontend et qui sera donc disponible publiquement. Mais dans notre cas d'utilisation il n'y a pas de risque particulier associé (je ne rentre pas en détail dans le fonctionnement de keycloak). Nous nous plions simplement à la configuration définie par Avenir(s).

### `VITE_TEST_MODE`
Elle est à true dans le fichier `env.ci` et lors de l'execution des tests End-to-End. Dans les faits cela permet de contourner tous les systèmes d'authentification et de pouvoir tester le frontend de manière totalement indépendante. Pas d'inquiétude à avoir en terme de sécurité vous ne pourrez pas faire d'appel à l'API backend. 