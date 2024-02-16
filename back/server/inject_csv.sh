#!/bin/bash
source secrets.sh
mvn -f.. compile exec:java -DDB_NAME=${DB_NAME} -DDB_PEM=${DB_PEM} -DDB_URI=${DB_URI} -DGOOGLE_OAUTH2_CLIENT_ID=${GOOGLE_OAUTH2_CLIENT_ID} -DGOOGLE_OAUTH2_CLIENT_SECRET=${GOOGLE_OAUTH2_CLIENT_SECRET} -DskipTests=true -Dexec.mainClass="fr.gouv.monprojetsup.tools.expeEns.ImportGroupsFromCsv"
