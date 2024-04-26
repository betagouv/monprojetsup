git pull
mvn compile exec:java -f../etl/pom.xml -DskipTests=true -Dexec.mainClass=fr.gouv.monprojetsup.data.update.UpdateMonProjetSupData
mvn -f ../pom.xml -P standalone -DskipTests=true compile install
pushd ../../../front/
npm run build
popd
#ln -s /home/gimbert/code/carte/js/dist/ /var/www/monprojetsup/carte
#ln -s /home/gimbert/code/donnees-carte/data/ /var/www/monprojetsup/carte/data
