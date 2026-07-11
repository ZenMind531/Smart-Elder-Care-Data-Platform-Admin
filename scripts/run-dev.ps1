$ErrorActionPreference = "Stop"

mvn -DskipTests package
java -jar target\smart-elder-care-0.0.1-SNAPSHOT.jar
