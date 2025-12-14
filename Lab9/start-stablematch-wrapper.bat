@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-25"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d %~dp0\..\Lab8
echo [STABLEMATCH-%1] Starting on port %1...
echo Using Java: %JAVA_HOME%
java -version
call mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=%1




