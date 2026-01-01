@echo off
REM ================================================
REM    DailyDose - Application Test Script
REM    এপ্লিকেশন টেস্ট স্ক্রিপ্ট
REM ================================================
echo.
echo ================================================
echo    DailyDose Application Check
echo    এপ্লিকেশন চেক করা হচ্ছে...
echo ================================================
echo.

cd /d "%~dp0"

echo [1/5] Checking Java Installation...
echo      Java ইনস্টলেশন চেক করা হচ্ছে...
java -version 2>&1 | findstr "version"
if %errorlevel% neq 0 (
    echo.
    echo ❌ ERROR: Java not found!
    echo    Java পাওয়া যায়নি!
    echo.
    echo Please install Java 17 or higher from:
    echo https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
echo    ✅ Java found
echo.

echo [2/5] Checking Maven Installation...
echo      Maven ইনস্টলেশন চেক করা হচ্ছে...
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo ⚠️  WARNING: Maven not found in PATH
    echo    Maven PATH এ নেই
    echo.
    echo Please use IntelliJ IDEA to run this application:
    echo IntelliJ IDEA ব্যবহার করুন:
    echo.
    echo 1. Open IntelliJ IDEA
    echo 2. File → Open → Select "Daily_Medicine_Remainder" folder
    echo 3. Wait for dependencies to download
    echo 4. Open DailyDoseApp.java
    echo 5. Right-click → Run 'DailyDoseApp.main()'
    echo.
    pause
    exit /b 0
)
echo    ✅ Maven found
echo.

echo [3/5] Checking Project Structure...
echo      প্রজেক্ট স্ট্রাকচার চেক করা হচ্ছে...
if not exist "pom.xml" (
    echo ❌ ERROR: pom.xml not found!
    pause
    exit /b 1
)
if not exist "src\main\java\com\example\DailyDoseApp.java" (
    echo ❌ ERROR: DailyDoseApp.java not found!
    pause
    exit /b 1
)
if not exist "src\main\resources\fxml\Login.fxml" (
    echo ❌ ERROR: Login.fxml not found!
    pause
    exit /b 1
)
echo    ✅ All required files found
echo.

echo [4/5] Compiling Application...
echo      এপ্লিকেশন কম্পাইল করা হচ্ছে...
echo.
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo.
    echo ❌ ERROR: Compilation failed!
    echo    কম্পাইলেশন ব্যর্থ!
    echo.
    echo Please check the errors above.
    echo উপরের errors দেখুন।
    pause
    exit /b 1
)
echo    ✅ Compilation successful
echo.

echo [5/5] Running Application...
echo      এপ্লিকেশন রান করা হচ্ছে...
echo.
echo ================================================
echo    Application Starting...
echo    এপ্লিকেশন শুরু হচ্ছে...
echo ================================================
echo.
echo Default Login Credentials:
echo ডিফল্ট লগইন তথ্য:
echo.
echo Patient:           Guardian:
echo Username: admin    Username: guardian
echo Password: admin123 Password: guard123
echo Role: Patient     Role: Guardian
echo.
echo ================================================
echo.

call mvn javafx:run

if %errorlevel% neq 0 (
    echo.
    echo ❌ Application failed to start!
    echo    এপ্লিকেশন চালু হয়নি!
    echo.
    echo Common solutions:
    echo 1. Make sure Java 17+ is installed
    echo 2. Try running from IntelliJ IDEA instead
    echo 3. Check if port is not already in use
    echo.
    pause
    exit /b 1
)

echo.
echo ✅ Application closed successfully
echo    এপ্লিকেশন সফলভাবে বন্ধ হয়েছে
echo.
pause
