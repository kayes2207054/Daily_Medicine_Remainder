@echo off
echo ========================================
echo Rebuilding and Running DailyDose App
echo ========================================
echo.

cd /d "%~dp0"

echo Deleting old build files...
rmdir /s /q target 2>nul
echo.

echo Please run the app from IntelliJ:
echo.
echo 1. In IntelliJ, go to: Build ^> Rebuild Project
echo 2. Wait for build to complete
echo 3. Run MainApp.java (right-click ^> Run 'MainApp.main()')
echo.
echo OR use Maven if configured:
echo    mvn clean compile exec:java -Dexec.mainClass="com.example.MainApp"
echo.
echo ========================================
echo All buttons will appear after rebuild!
echo ========================================
pause
