# MonProjetSup v1

## Content of the folder
- `etl` a maven module allowing to extract transform and load reference data
- `suggestions` a maven module containing the suggestions service
- `app` a maven module containing the web application (pPoC version)
- `common` a maven module containing dto, some parcoursup libs, and a generic model for a service used in etl, app and suggestions
- `dataConfig.json`  json file containing the configuration of the data source
- `libs` a folder containing the parcoursup libraries used in the project
- `config` a folder containing samples of the configuration files used in the project, that should be accessible from within the starting directories


## How to build

### prerequities

https://github.com/nodesource/distributions#installation-instructions

sudo apt install mvn openjdk-19-jdk openjdk-19-jre

### Install the jars

From within `monprojetsup/`.

```
mvn install:install-file -Dfile=java/libs/parcoursup-carte-2023-3-orientation.jar -DpomFile=java/libs/parcoursup-carte-2023-3-orientation.pom
```

```
mvn install:install-file -Dfile=java/libs/parcoursup-algos-2023-4.jar -DpomFile=java/libs/parcoursup-algos-2023-4.pom
```

### Generates the data

From within `monprojetsup/etl`.
```
mvn compile exec:java -f../pom.xml -Dexec.mainClass=fr.gouv.monprojetsup.data.update.UpdateMonProjetSupData
```
That will produce two files `frontendData.json` and `backendData.json` in the running folder.

### Generates the front dist

From within `monprojetsup/front`.
```
npm install
npm run build
```
