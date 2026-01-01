#!/bin/bash
# ================================================
#    DailyDose - Quick Application Checker
#    দ্রুত এপ্লিকেশন চেকার
# ================================================

echo "================================================"
echo "   DailyDose - Application Health Check"
echo "   এপ্লিকেশন হেলথ চেক"
echo "================================================"
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

ERRORS=0

# Check 1: Java Files
echo "[CHECK 1] Verifying JavaFX Files..."
if [ ! -f "src/main/java/com/example/DailyDoseApp.java" ]; then
    echo -e "${RED}❌ DailyDoseApp.java not found${NC}"
    ERRORS=$((ERRORS+1))
else
    echo -e "${GREEN}✅ DailyDoseApp.java${NC}"
fi

if [ ! -f "src/main/java/com/example/viewfx/LoginController.java" ]; then
    echo -e "${RED}❌ LoginController.java not found${NC}"
    ERRORS=$((ERRORS+1))
else
    echo -e "${GREEN}✅ LoginController.java${NC}"
fi

if [ ! -f "src/main/java/com/example/viewfx/PatientDashboardController.java" ]; then
    echo -e "${RED}❌ PatientDashboardController.java not found${NC}"
    ERRORS=$((ERRORS+1))
else
    echo -e "${GREEN}✅ PatientDashboardController.java${NC}"
fi

if [ ! -f "src/main/java/com/example/viewfx/GuardianDashboardController.java" ]; then
    echo -e "${RED}❌ GuardianDashboardController.java not found${NC}"
    ERRORS=$((ERRORS+1))
else
    echo -e "${GREEN}✅ GuardianDashboardController.java${NC}"
fi

if [ ! -f "src/main/java/com/example/viewfx/AlarmService.java" ]; then
    echo -e "${RED}❌ AlarmService.java not found${NC}"
    ERRORS=$((ERRORS+1))
else
    echo -e "${GREEN}✅ AlarmService.java${NC}"
fi

# Check 2: FXML Files
echo ""
echo "[CHECK 2] Verifying FXML Files..."
if [ ! -f "src/main/resources/fxml/Login.fxml" ]; then
    echo -e "${RED}❌ Login.fxml not found${NC}"
    ERRORS=$((ERRORS+1))
else
    echo -e "${GREEN}✅ Login.fxml${NC}"
fi

if [ ! -f "src/main/resources/fxml/Registration.fxml" ]; then
    echo -e "${RED}❌ Registration.fxml not found${NC}"
    ERRORS=$((ERRORS+1))
else
    echo -e "${GREEN}✅ Registration.fxml${NC}"
fi

if [ ! -f "src/main/resources/fxml/PatientDashboard.fxml" ]; then
    echo -e "${RED}❌ PatientDashboard.fxml not found${NC}"
    ERRORS=$((ERRORS+1))
else
    echo -e "${GREEN}✅ PatientDashboard.fxml${NC}"
fi

if [ ! -f "src/main/resources/fxml/GuardianDashboard.fxml" ]; then
    echo -e "${RED}❌ GuardianDashboard.fxml not found${NC}"
    ERRORS=$((ERRORS+1))
else
    echo -e "${GREEN}✅ GuardianDashboard.fxml${NC}"
fi

# Check 3: CSS
echo ""
echo "[CHECK 3] Verifying CSS Files..."
if [ ! -f "src/main/resources/css/style.css" ]; then
    echo -e "${RED}❌ style.css not found${NC}"
    ERRORS=$((ERRORS+1))
else
    echo -e "${GREEN}✅ style.css${NC}"
fi

# Check 4: Controllers
echo ""
echo "[CHECK 4] Verifying Business Controllers..."
CONTROLLERS=("MedicineController" "ReminderController" "HistoryController" "UserController")
for controller in "${CONTROLLERS[@]}"; do
    if [ ! -f "src/main/java/com/example/controller/${controller}.java" ]; then
        echo -e "${RED}❌ ${controller}.java not found${NC}"
        ERRORS=$((ERRORS+1))
    else
        echo -e "${GREEN}✅ ${controller}.java${NC}"
    fi
done

# Check 5: Models
echo ""
echo "[CHECK 5] Verifying Models..."
MODELS=("Medicine" "Reminder" "DoseHistory" "User" "GuardianPatientLink" "Notification")
for model in "${MODELS[@]}"; do
    if [ ! -f "src/main/java/com/example/model/${model}.java" ]; then
        echo -e "${RED}❌ ${model}.java not found${NC}"
        ERRORS=$((ERRORS+1))
    else
        echo -e "${GREEN}✅ ${model}.java${NC}"
    fi
done

# Check 6: Database
echo ""
echo "[CHECK 6] Verifying Database Manager..."
if [ ! -f "src/main/java/com/example/database/DatabaseManager.java" ]; then
    echo -e "${RED}❌ DatabaseManager.java not found${NC}"
    ERRORS=$((ERRORS+1))
else
    echo -e "${GREEN}✅ DatabaseManager.java${NC}"
fi

# Check 7: pom.xml
echo ""
echo "[CHECK 7] Verifying Maven Configuration..."
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}❌ pom.xml not found${NC}"
    ERRORS=$((ERRORS+1))
else
    # Check for JavaFX dependencies
    if grep -q "javafx-controls" pom.xml && grep -q "javafx-fxml" pom.xml && grep -q "javafx-media" pom.xml; then
        echo -e "${GREEN}✅ pom.xml with JavaFX dependencies${NC}"
    else
        echo -e "${YELLOW}⚠️  pom.xml missing some JavaFX dependencies${NC}"
        ERRORS=$((ERRORS+1))
    fi
fi

# Final Report
echo ""
echo "================================================"
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ ALL CHECKS PASSED!${NC}"
    echo -e "${GREEN}   সব চেক সফল!${NC}"
    echo ""
    echo "Your application is ready to run!"
    echo "আপনার এপ্লিকেশন চালানোর জন্য প্রস্তুত!"
    echo ""
    echo "To run:"
    echo "  - IntelliJ: Right-click DailyDoseApp.java → Run"
    echo "  - Terminal: mvn javafx:run"
else
    echo -e "${RED}❌ FOUND $ERRORS ERRORS${NC}"
    echo -e "${RED}   $ERRORS টি সমস্যা পাওয়া গেছে${NC}"
    echo ""
    echo "Please fix the missing files before running."
fi
echo "================================================"
