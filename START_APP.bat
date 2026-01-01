@echo off
REM DailyDose - Medicine Tracker Application (JavaFX)
REM Standalone launcher that doesn't depend on Maven or IntelliJ

setlocal enabledelayedexpansion

REM Set your Java path here
set JAVA_HOME=C:\Users\CILONIA IT\.jdks\openjdk-25.0.1
set JAVA_EXE=!JAVA_HOME!\bin\java.exe

REM Maven repository path
set M2_REPO=C:\Users\CILONIA IT\.m2\repository

REM Project directory
set PROJECT_DIR=%~dp0

REM Build directory
set BUILD_DIR=!PROJECT_DIR!target\classes

echo.
echo ========================================
echo  DailyDose - Medicine Tracker (JavaFX)
echo ========================================
echo.

REM Check if classes are compiled
if not exist "!BUILD_DIR!" (
    echo Compiling application...
    cd /d "!PROJECT_DIR!"
    call mvn clean compile -q -DskipTests
    if errorlevel 1 (
        echo Compilation failed!
        pause
        exit /b 1
    )
)

REM Set module path (JavaFX libraries)
set MODULE_PATH=!M2_REPO!\org\openjfx\javafx-controls\17.0.2
set MODULE_PATH=!MODULE_PATH!;!M2_REPO!\org\openjfx\javafx-fxml\17.0.2
set MODULE_PATH=!MODULE_PATH!;!M2_REPO!\org\openjfx\javafx-graphics\17.0.2
set MODULE_PATH=!MODULE_PATH!;!M2_REPO!\org\openjfx\javafx-base\17.0.2

REM Build classpath (all dependencies)
set CLASSPATH=!BUILD_DIR!
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\xerial\sqlite-jdbc\3.44.0.0\sqlite-jdbc-3.44.0.0.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\jfree\jfreechart\1.5.3\jfreechart-1.5.3.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\apache\commons\commons-csv\1.10.0\commons-csv-1.10.0.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\apache\commons\commons-lang3\3.14.0\commons-lang3-3.14.0.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\slf4j\slf4j-api\2.0.11\slf4j-api-2.0.11.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\ch\qos\logback\logback-classic\1.4.14\logback-classic-1.4.14.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\ch\qos\logback\logback-core\1.4.14\logback-core-1.4.14.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2-win.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2-win.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\openjfx\javafx-graphics\17.0.2\javafx-graphics-17.0.2.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\openjfx\javafx-graphics\17.0.2\javafx-graphics-17.0.2-win.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\openjfx\javafx-base\17.0.2\javafx-base-17.0.2.jar
set CLASSPATH=!CLASSPATH!;!M2_REPO!\org\openjfx\javafx-base\17.0.2\javafx-base-17.0.2-win.jar

echo Starting application with proper JavaFX module path...
echo.

REM Run Java with proper module path
"!JAVA_EXE!" ^
    --module-path "!MODULE_PATH!" ^
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
    -cp "!CLASSPATH!" ^
    com.example.MainApp

if errorlevel 1 (
    echo.
    echo Application failed to start!
    echo Check that all paths are correct in this batch file.
    pause
    exit /b 1
)

endlocal
