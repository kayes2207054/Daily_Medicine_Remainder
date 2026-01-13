@echo off
cd /d "%~dp0"
powershell -ExecutionPolicy Bypass -File "Run_Swing_Auto.ps1"
if %errorlevel% neq 0 pause
