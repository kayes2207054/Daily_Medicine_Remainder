@echo off
echo ========================================
echo IntelliJ Cache Fix for Build Errors
echo ========================================
echo.
echo This will help resolve "cannot access" errors in IntelliJ
echo.
echo STEP 1: Close IntelliJ IDEA completely
pause
echo.
echo STEP 2: Deleting target directory...
rmdir /s /q "target" 2>nul
echo Done!
echo.
echo STEP 3: Now open IntelliJ IDEA and follow these steps:
echo   1. Go to File ^> Invalidate Caches
echo   2. Check ALL boxes:
echo      - Clear file system cache and Local History
echo      - Clear downloaded shared indexes
echo      - Clear VCS Log caches and indexes
echo   3. Click "Invalidate and Restart"
echo.
echo STEP 4: After restart, go to Build ^> Rebuild Project
echo.
echo ========================================
echo Your build errors should now be fixed!
echo ========================================
pause
