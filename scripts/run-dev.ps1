$ErrorActionPreference = "Stop"

mvn -DskipTests package
java -jar target\smart-eldercare-data-platform-admin-0.0.1-SNAPSHOT.jar
