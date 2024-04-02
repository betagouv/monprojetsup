<<<<<<< HEAD
# MonProjetSup

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
=======
## Content of the repo

- data/: data from external source
- back/: 
	- code for the back (mostly java) 
	- server scripts
	+ server data files generated from external data files in data/
- front: 
	- code for the front (mostly html + js)
	- front data files generated from external data files in data/
- sandbox: the place to play with data. Includes expertiments on keywords extraction and learning admission models.



## MonProjetSup

To run MonProjetSup locally on your machine,
refer to front/README.md and back/README.md

Documentation is available on the project wiki:
https://gitlab.com/parcoursup/parcoursup_orientation/-/wikis/home

## Guidelines with data


Les fichiers de données d'origine externe sont dans
	orientation/monprojetsup/data

Ces fichiers externes sont utilisés par 
	fr.gouv.monprojetsup.data.back.UpdateBackData
pour générer des fichiers utilisés par le back,
ces fichiers sont dans back/data.
	

Ces fichiers externes sont utilisés par 
	fr.gouv.monprojetsup.data.front.UpdateFrontData
pour générer des fichiers utilisés par le front,
ces fichiers sont 
	front/src/data/data.zip
>>>>>>> origin/prod
