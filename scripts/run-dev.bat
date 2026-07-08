@echo off
call mvn -DskipTests package
if errorlevel 1 exit /b %errorlevel%
java -jar target\smart-eldercare-data-platform-admin-0.0.1-SNAPSHOT.jar
