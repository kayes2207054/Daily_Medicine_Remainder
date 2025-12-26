@echo off
REM DailyDose - Medicine Tracker Application Launcher
REM This script builds and runs the application without needing IntelliJ.

echo Running DailyDose (JavaFX) via Maven...
call mvn -q -DskipTests javafx:run

if errorlevel 1 (
    echo JavaFX run failed. Trying exec:java...
    call mvn -q -DskipTests -Dexec.mainClass=com.example.MainApp exec:java
)

pause
