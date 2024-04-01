git pull
mvn -f ../pom.xml -DskipTests=true clean compile package
#mvn -f ../pom.xml -DskipTests=true -P standalone compile install
sudo service mps restart
