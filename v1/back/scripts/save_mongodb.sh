pushd /home/gimbert/code/monprojetsup_poc2/v1/back/java/tmp/
source secrets.sh
mongodump  --uri ${DB_URI} --db ${DB_NAME}
tar -czf dump.tgz dump

mvn compile exec:java -f../app/pom.xml -DDB_NAME=${DB_NAME} -DDB_PEM=${DB_PEM} -DDB_URI=${DB_URI} -DGOOGLE_OAUTH2_CLIENT_ID=${GOOGLE_OAUTH2_CLIENT_ID} -DGOOGLE_OAUTH2_CLIENT_SECRET=${GOOGLE_OAUTH2_CLIENT_SECRET} -DskipTests=true -Dexec.mainClass=fr.gouv.monprojetsup.app.db.admin.ExportTraces
now=$(date +"%Y_%m_%d_%I_%M_%p")
cp traces.json data/traces_$now.json
tar -czf traces.tgz data/traces_$now.json

popd
