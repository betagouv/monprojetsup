#!/bin/bash
mvn clean install -f../common/pom.xml
mvn clean install -f../etl/pom.xml
mvn clean compile exec:java -f../suggestions/pom.xml -Dexec.mainClass="fr.gouv.monprojetsup.suggestions.ApplicationSuggestionsKt"
