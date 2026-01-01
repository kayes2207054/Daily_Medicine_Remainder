@echo off
REM DailyDose - Medicine Tracker Application Launcher (JavaFX)
REM This script builds and runs the application with proper JavaFX module path

echo.
echo ========================================
echo DailyDose Medicine Tracker - JavaFX App
echo ========================================
echo.

setlocal enabledelayedexpansion

REM Get Maven repository location
if defined MAVEN_HOME (
    set "REPO=%MAVEN_HOME%\..\repository"
) else (
    set "REPO=%USERPROFILE%\.m2\repository"
)

REM Build the app
echo Compiling application...
call mvn clean compile -q -DskipTests

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Starting JavaFX application...
echo.

REM Run with proper module path
call mvn exec:java@run-app -DskipTests

if errorlevel 1 (
    echo.
    echo If Maven failed, trying fallback method...
    cd target\classes
    java --module-path "!REPO!\org\openjfx\javafx-controls\17.0.2;!REPO!\org\openjfx\javafx-fxml\17.0.2;!REPO!\org\openjfx\javafx-graphics\17.0.2;!REPO!\org\openjfx\javafx-base\17.0.2" ^
         --add-modules javafx.controls,javafx.fxml ^
         -cp "..\..\pom.xml" com.example.MainApp
    cd ..\..
)

endlocal
pause
