@echo off
REM DailyDose - Medicine Tracker Application Launcher
REM No Maven required - just Java

setlocal enabledelayedexpansion

set JAVA_HOME=C:\Users\CILONIA IT\.jdks\openjdk-25.0.1
set JAVA_EXE=!JAVA_HOME!\bin\java.exe
set JAVAC_EXE=!JAVA_HOME!\bin\javac.exe
set REPO=C:\Users\CILONIA IT\.m2\repository
set APP_DIR=%~dp0
set SRC=!APP_DIR!src\main\java
set CLASSES=!APP_DIR!target\classes
set RESOURCES=!APP_DIR!src\main\resources

echo.
echo ========================================
echo DailyDose - Medicine Tracker (JavaFX)
echo ========================================
echo.

REM Check if Java exists
if not exist "!JAVA_EXE!" (
    echo ERROR: Java not found at !JAVA_EXE!
    pause
    exit /b 1
)

REM Create target/classes if missing
if not exist "!CLASSES!" mkdir "!CLASSES!"

REM Copy resources (FXML files)
if exist "!RESOURCES!" (
    echo Copying resources...
    xcopy "!RESOURCES!\*" "!CLASSES!\" /S /Y /Q > nul 2>&1
)

REM Compile
echo Compiling Java sources...
for /r "!SRC!" %%f in (*.java) do (
    "!JAVAC_EXE!" -d "!CLASSES!" -cp "!CLASSES!" "%%f" 2>nul
)

REM Build module-path (JavaFX)
set MODULE_PATH=!REPO!\org\openjfx\javafx-base\17.0.2\javafx-base-17.0.2.jar;!REPO!\org\openjfx\javafx-base\17.0.2\javafx-base-17.0.2-win.jar;!REPO!\org\openjfx\javafx-graphics\17.0.2\javafx-graphics-17.0.2.jar;!REPO!\org\openjfx\javafx-graphics\17.0.2\javafx-graphics-17.0.2-win.jar;!REPO!\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2.jar;!REPO!\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2-win.jar;!REPO!\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2.jar;!REPO!\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2-win.jar

REM Build classpath
set CLASSPATH=!CLASSES!;!REPO!\org\xerial\sqlite-jdbc\3.44.0.0\sqlite-jdbc-3.44.0.0.jar;!REPO!\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar;!REPO!\org\jfree\jfreechart\1.5.3\jfreechart-1.5.3.jar;!REPO!\org\apache\commons\commons-csv\1.10.0\commons-csv-1.10.0.jar;!REPO!\org\apache\commons\commons-lang3\3.14.0\commons-lang3-3.14.0.jar;!REPO!\org\slf4j\slf4j-api\2.0.11\slf4j-api-2.0.11.jar;!REPO!\ch\qos\logback\logback-classic\1.4.14\logback-classic-1.4.14.jar;!REPO!\ch\qos\logback\logback-core\1.4.14\logback-core-1.4.14.jar

echo Starting application...
echo.

"!JAVA_EXE!" ^
    --module-path "!MODULE_PATH!" ^
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base ^
    -cp "!CLASSPATH!" ^
    com.example.MainApp

endlocal
pause
