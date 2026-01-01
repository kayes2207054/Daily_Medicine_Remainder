@echo off
REM ================================================================
REM    🚀 DailyDose - Medicine Tracker Application
REM    এই ফাইলে Double Click করে Application চালান
REM ================================================================
echo.
echo ================================================================
echo    🚀 DailyDose - Personal Medicine Companion
echo    ঔষধ ট্র্যাকার এপ্লিকেশন
echo ================================================================
echo.

cd /d "%~dp0"

REM Check if running from IntelliJ is better
echo 📋 Checking system...
echo    সিস্টেম চেক করা হচ্ছে...
echo.

REM Check Maven
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo ⚠️  Maven PATH এ নেই!
    echo.
    echo ✅ সবচেয়ে সহজ উপায়: IntelliJ IDEA ব্যবহার করুন
    echo ================================
    echo.
    echo 📝 IntelliJ এ কিভাবে চালাবেন:
    echo.
    echo 1. IntelliJ IDEA খুলুন
    echo 2. File → Open
    echo 3. এই folder select করুন: Daily_Medicine_Remainder
    echo 4. 2-3 মিনিট wait করুন (dependencies download হবে)
    echo 5. src/main/java/com/example/DailyDoseApp.java খুলুন
    echo 6. File এ Right-click করে "Run 'DailyDoseApp.main()'" select করুন
    echo.
    echo ================================
    echo.
    echo অথবা...
    echo.
    echo ✅ Maven Install করুন:
    echo    https://maven.apache.org/download.cgi
    echo    তারপর এই file আবার double-click করুন
    echo.
    echo ================================
    echo.
    pause
    
    REM Try to open in IntelliJ if installed
    echo.
    echo 🔍 IntelliJ খোঁজা হচ্ছে...
    
    if exist "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3\bin\idea64.exe" (
        echo ✅ IntelliJ পাওয়া গেছে! Opening project...
        start "" "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3\bin\idea64.exe" "%~dp0"
    ) else if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2023.3\bin\idea64.exe" (
        echo ✅ IntelliJ পাওয়া গেছে! Opening project...
        start "" "C:\Program Files\JetBrains\IntelliJ IDEA 2023.3\bin\idea64.exe" "%~dp0"
    ) else (
        echo ℹ️  IntelliJ খুঁজে পাওয়া যায়নি
        echo    ম্যানুয়ালি IntelliJ খুলে এই folder open করুন
    )
    
    exit /b 0
)

echo ✅ Maven পাওয়া গেছে!
echo.
echo 🔨 Compiling application...
echo    এপ্লিকেশন কম্পাইল করা হচ্ছে...
echo.

call mvn clean compile -q

if %errorlevel% neq 0 (
    echo.
    echo ❌ Compilation failed!
    echo    কম্পাইলেশন ব্যর্থ হয়েছে!
    echo.
    echo Please check errors above or try running in IntelliJ
    echo উপরের errors দেখুন অথবা IntelliJ এ চালান
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.
echo 🚀 Starting application...
echo    এপ্লিকেশন চালু হচ্ছে...
echo.
echo ================================================================
echo    Login Credentials (লগইন তথ্য):
echo ================================================================
echo.
echo    👤 Patient (রোগী):           👨‍👧 Guardian (অভিভাবক):
echo       Username: admin              Username: guardian
echo       Password: admin123           Password: guard123
echo       Role: Patient                Role: Guardian
echo.
echo ================================================================
echo.

call mvn javafx:run

if %errorlevel% neq 0 (
    echo.
    echo ❌ Application failed to start!
    echo.
    echo Try running in IntelliJ IDEA instead
    echo IntelliJ IDEA এ চালানোর চেষ্টা করুন
    pause
    exit /b 1
)

echo.
echo ✅ Application closed
pause
