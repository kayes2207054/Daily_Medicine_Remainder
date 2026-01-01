@echo off
REM Run JavaFX app without Maven (direct java call)
REM Assumes classes are already compiled to target/classes

setlocal enabledelayedexpansion

set JAVA_HOME=C:\Users\CILONIA IT\.jdks\openjdk-25.0.1
set JAVA_EXE=!JAVA_HOME!\bin\java.exe
set REPO=C:\Users\CILONIA IT\.m2\repository
set APPDIR=%~dp0
set CLASSES=%APPDIR%target\classes

if not exist "%CLASSES%" (
  echo target\classes missing. Please build first (mvn compile).
  pause
  exit /b 1
)

set MODULE_PATH=!REPO!\org\openjfx\javafx-base\17.0.2\javafx-base-17.0.2.jar;!REPO!\org\openjfx\javafx-base\17.0.2\javafx-base-17.0.2-win.jar;!REPO!\org\openjfx\javafx-graphics\17.0.2\javafx-graphics-17.0.2.jar;!REPO!\org\openjfx\javafx-graphics\17.0.2\javafx-graphics-17.0.2-win.jar;!REPO!\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2.jar;!REPO!\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2-win.jar;!REPO!\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2.jar;!REPO!\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2-win.jar

set CP=!CLASSES!
set CP=!CP!;!REPO!\org\xerial\sqlite-jdbc\3.44.0.0\sqlite-jdbc-3.44.0.0.jar
set CP=!CP!;!REPO!\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar
set CP=!CP!;!REPO!\org\jfree\jfreechart\1.5.3\jfreechart-1.5.3.jar
set CP=!CP!;!REPO!\org\apache\commons\commons-csv\1.10.0\commons-csv-1.10.0.jar
set CP=!CP!;!REPO!\org\apache\commons\commons-lang3\3.14.0\commons-lang3-3.14.0.jar
set CP=!CP!;!REPO!\org\slf4j\slf4j-api\2.0.11\slf4j-api-2.0.11.jar
set CP=!CP!;!REPO!\ch\qos\logback\logback-classic\1.4.14\logback-classic-1.4.14.jar
set CP=!CP!;!REPO!\ch\qos\logback\logback-core\1.4.14\logback-core-1.4.14.jar

"!JAVA_EXE!" ^
  --module-path "!MODULE_PATH!" ^
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
  -cp "!CP!" ^
  com.example.MainApp

endlocal
