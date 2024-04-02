# Mon Projet Sup

Ce repository est un mono-repo qui contient plusieurs applications nécessaires au fonctionnement de Mon Projet Sup

## Applications contenu dans le repo
- **v1** - l'application historique (ce dossier est amené à disparaitre)
- **etl** - Permet de transformer les fichiers référentiels en JSON afin de les écrire dans une base Redis utilisée par toutes les applications
- **suggestions** - API permettant de générer des suggestions de formations
- **app** - coeur de l'application Mon Projet Sup
  - front - Frontend de tout le projet 
  - back - API avec laquelle communique le frontend


## Comment démarrer
À la racine du projet lancer la commande : 
```bash
docker compose -f docker-compose.dev.yml up
```
