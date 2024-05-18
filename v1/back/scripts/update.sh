pushd /home/gimbert/code/monprojetsup/data/data
git pull
popd
git pull
mvn compile exec:java -f../etl/pom.xml -DskipTests=true -Dexec.mainClass=fr.gouv.monprojetsup.data.update.UpdateMonProjetSupData
pushd /home/gimbert/code/monprojetsup_poc2/v1/front
npm run build
popd
#ln -s /home/gimbert/code/carte/js/dist/ /var/www/monprojetsup/carte
#ln -s /home/gimbert/code/donnees-carte/data/ /var/www/monprojetsup/carte/data
