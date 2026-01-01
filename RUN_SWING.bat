@echo off
REM DailyDose - Swing Version Runner
cd /d "%~dp0"
echo Starting DailyDose (Swing UI)...
echo.
java -cp "target/classes;C:\Users\CILONIA IT\.m2\repository\org\xerial\sqlite-jdbc\3.44.0.0\sqlite-jdbc-3.44.0.0.jar;C:\Users\CILONIA IT\.m2\repository\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar;C:\Users\CILONIA IT\.m2\repository\org\jfree\jfreechart\1.5.3\jfreechart-1.5.3.jar;C:\Users\CILONIA IT\.m2\repository\org\apache\commons\commons-csv\1.10.0\commons-csv-1.10.0.jar;C:\Users\CILONIA IT\.m2\repository\org\apache\commons\commons-lang3\3.14.0\commons-lang3-3.14.0.jar;C:\Users\CILONIA IT\.m2\repository\org\slf4j\slf4j-api\2.0.11\slf4j-api-2.0.11.jar;C:\Users\CILONIA IT\.m2\repository\ch\qos\logback\logback-classic\1.4.14\logback-classic-1.4.14.jar;C:\Users\CILONIA IT\.m2\repository\ch\qos\logback\logback-core\1.4.14\logback-core-1.4.14.jar" com.example.Main
pause
