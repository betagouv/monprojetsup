git pull
mvn -f ../common/pom.xml -DskipTests=true clean compile install
mvn -f ../etl/pom.xml -DskipTests=true clean compile install
mvn -f ../pom.xml -DskipTests=true clean compile package
#mvn -f ../pom.xml -DskipTests=true -P standalone compile install
sudo service mps restart
sudo service sugg restart
