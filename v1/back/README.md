# MonProjetSup

## Content of the folder

- `java`: src code of the server.
- `scripts`: scripts useful on the linux server

Inside the `java`folder
- `etl` a maven module allowing to extract transform and load reference data
- `suggestions` a maven module containing the suggestions service
- `app` a maven module containing the web application (pPoC version)
- `common` a maven module containing dto, some parcoursup libs, and a generic model for a service used in etl, app and suggestions
- `dataConfig.json`  json file containing the configuration of the data source
- `libs` a folder containing the parcoursup libraries used in the project
- `config` a folder containing samples of the configuration files used in the project, that should be accessible from within the starting directories


## Running the server

### prerequities

https://github.com/nodesource/distributions#installation-instructions

sudo apt install mvn openjdk-19-jdk openjdk-19-jre

### Install the jars in the local maven repo

From within `monprojetsup/back/`.

```
mvn install:install-file -Dfile=java/libs/parcoursup-carte-2023-3-orientation.jar -DpomFile=java/libs/parcoursup-carte-2023-3-orientation.pom
```

```
mvn install:install-file -Dfile=java/libs/parcoursup-algos-2023-4.jar -DpomFile=java/libs/parcoursup-algos-2023-4.pom
```

### Generates the data

From within a folder, e.g. `monprojetsup/back/java/tmp`, containing the configuration file `dataConfig.json`given in example in java/config,
pointing to the folder containing the data. 

```
mvn compile exec:java -f../etl/pom.xml -Dexec.mainClass=fr.gouv.monprojetsup.data.update.UpdateMonProjetSupData
```


### Generates the front dist

From within `monprojetsup/front`.
```
npm install
npm run build
```

### Install the dependencies jars

```cd etl && mvn install```
```cd common && mvn install```

### Start the suggestion server

From within a temporary working directory, typically `monprojetsup/back/java/tmp`.

- On a server: run the script `monprojetsup/back/server/start_suggestions.sh`
- On a local computer

`mvn clean compile exec:java -f../suggestions/pom.xml -Dexec.mainClass=fr.gouv.monprojetsup.suggestions.ApplicationSuggestionsKt`


### Start the app 

From within a temporary working directory, typically `monprojetsup/back/java/tmp`.

- On a server: run the script `monprojetsup/back/server/start_server.sh`
- On a local computer

`mvn clean compile exec:java -f../app/pom.xml -Dexec.mainClass=fr.gouv.monprojetsup.app.ApplicationMpsKt`

### 	

Create as many accounts as you want (moderation is not activated by default).
To set a user as teacher, you need an admin user.
To set a user as admin, check next section.

### Server configuration

A config file named `serverConfig.json` will automatically be created in the directory
from where the server is run. You can edit it to set the admins.


## Updating MonProjetSup data

### [optional] update parcoursup data 
Requires a direct connection to psup database

```
mvn exec:java -f../etl/pom.xml -Dexec.mainClass=fr.gouv.monprojetsup.data.update.psup.UpdateParcoursupDataFromPsupDB
```


### update data files used by back and front
The update uses content of the `monprojetsup/data`folder to generate datafiles in the front and back folder.

```
mvn exec:java -f../etl/pom.xml -Dexec.mainClass=fr.gouv.monprojetsup.data.UpdateMonProjetSupData
```



### update the etl postgre database of the v2 of the app



Install the jar fr.gouv.monprojetsup-entities by typing, from the root of the directory

```
mvn -f "app/back/pom.xml" clean package install -DskipTests=true
```

Set the environment variable `MPS_DATA_DIR` to the path containing the data files,
typicall the directory created when cloning the data git repo.

Also set the environment variables APP_DB_HOST_NAME, APP_DB_PORT_EXPOSED, APP_DB_NAME, APP_DB_USER
necessary to access the db.


```
mvn clean install -f"v1/back/java/pom.xml"
mvn exec:java -f "v1/back/java/etl/pom.xml" -Dexec.mainClass=fr.gouv.monprojetsup.etl.ApplicationEtlKt
```
