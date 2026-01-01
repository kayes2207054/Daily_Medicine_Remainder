# DailyDose JavaFX Launcher (PowerShell)
# Robust launcher that compiles and runs the app

$ErrorActionPreference = "Stop"

# Paths
$JAVA_HOME = "C:\Users\CILONIA IT\.jdks\openjdk-25.0.1"
$JAVA_EXE = "$JAVA_HOME\bin\java.exe"
$JAVAC_EXE = "$JAVA_HOME\bin\javac.exe"
$REPO = "C:\Users\CILONIA IT\.m2\repository"
$APP_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
$SRC_DIR = "$APP_DIR\src\main\java"
$CLASSES_DIR = "$APP_DIR\target\classes"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "DailyDose Medicine Tracker - JavaFX App" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check Java
if (!(Test-Path $JAVA_EXE)) {
    Write-Host "ERROR: Java not found at $JAVA_EXE" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Step 1: Compile (if needed)
if (!(Test-Path $CLASSES_DIR)) {
    Write-Host "Classes not found. Compiling..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $CLASSES_DIR -Force | Out-Null
    
    $SOURCES = Get-ChildItem -Path $SRC_DIR -Filter "*.java" -Recurse
    if ($SOURCES.Count -eq 0) {
        Write-Host "ERROR: No Java source files found" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
    
    Write-Host "Compiling " $SOURCES.Count " files..." -ForegroundColor Yellow
    & $JAVAC_EXE -d $CLASSES_DIR -cp $CLASSES_DIR (Get-ChildItem -Path $SRC_DIR -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName) 2>&1
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Compilation failed" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
    Write-Host "Compilation successful" -ForegroundColor Green
}

# Step 2: Build module path
$MODULE_PATH = @(
    "$REPO\org\openjfx\javafx-base\17.0.2\javafx-base-17.0.2.jar",
    "$REPO\org\openjfx\javafx-base\17.0.2\javafx-base-17.0.2-win.jar",
    "$REPO\org\openjfx\javafx-graphics\17.0.2\javafx-graphics-17.0.2.jar",
    "$REPO\org\openjfx\javafx-graphics\17.0.2\javafx-graphics-17.0.2-win.jar",
    "$REPO\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2.jar",
    "$REPO\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2-win.jar",
    "$REPO\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2.jar",
    "$REPO\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2-win.jar"
) -join ";"

# Step 3: Build classpath
$CLASSPATH = @(
    $CLASSES_DIR,
    "$REPO\org\xerial\sqlite-jdbc\3.44.0.0\sqlite-jdbc-3.44.0.0.jar",
    "$REPO\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar",
    "$REPO\org\jfree\jfreechart\1.5.3\jfreechart-1.5.3.jar",
    "$REPO\org\apache\commons\commons-csv\1.10.0\commons-csv-1.10.0.jar",
    "$REPO\org\apache\commons\commons-lang3\3.14.0\commons-lang3-3.14.0.jar",
    "$REPO\org\slf4j\slf4j-api\2.0.11\slf4j-api-2.0.11.jar",
    "$REPO\ch\qos\logback\logback-classic\1.4.14\logback-classic-1.4.14.jar",
    "$REPO\ch\qos\logback\logback-core\1.4.14\logback-core-1.4.14.jar"
) -join ";"

Write-Host "Starting JavaFX application..." -ForegroundColor Green
Write-Host ""

# Step 4: Run
& $JAVA_EXE `
    --module-path "$MODULE_PATH" `
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base `
    -cp "$CLASSPATH" `
    com.example.MainApp

$EXIT_CODE = $LASTEXITCODE
if ($EXIT_CODE -ne 0) {
    Write-Host ""
    Write-Host "Application exited with code $EXIT_CODE" -ForegroundColor Yellow
}
