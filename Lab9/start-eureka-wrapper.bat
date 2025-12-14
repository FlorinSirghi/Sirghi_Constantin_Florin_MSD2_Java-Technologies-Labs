@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-25"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d %~dp0
echo [EUREKA] Starting on port 8761...
echo Using Java: %JAVA_HOME%
java -version
call mvnw.cmd spring-boot:run -Dspring-boot.run.main-class=com.example.Lab9.eureka.EurekaServerApplication -Dspring-boot.run.arguments=--spring.profiles.active=eureka




