═══════════════════════════════════════════════════════════════════════════════
    BANKING APPLICATION SIMULATOR - README
    Testing Procedures for Operational Issues - Sprint 3
═══════════════════════════════════════════════════════════════════════════════

PROJECT INFORMATION
═══════════════════════════════════════════════════════════════════════════════
Project Name:    Testing Procedures for Operational Issues
Sprint:          Sprint 3 - Develop
Developer:       Jorge Pena
Company:         REM Consultancy
Date:            November 2025
Version:         1.0


DESCRIPTION
═══════════════════════════════════════════════════════════════════════════════
This Java application simulates a banking system that validates user input data
for electronic banking operations. The simulator implements black-box testing
using equivalence classes and test cases to ensure data accuracy and quality.

The application validates five key parameters:
1. Bank Code: 3-digit numeric code (000-999)
2. Branch Code: 4-digit numeric code (0000-9999)
3. Account Number: 10-digit numeric code
4. Personal Key: Alphanumeric, minimum 8 characters
5. Order Value: "Checkbook" or "Statement"


FILES INCLUDED
═══════════════════════════════════════════════════════════════════════════════
1. BankingApplicationSimulator.java - Main Java source code with 12 test cases
2. run_tests.ps1 - PowerShell script for automated test execution
3. run_tests.bat - Batch script for automated test execution
4. README.txt - This file (complete documentation)
5. 12 Test Cases - Test cases.pdf - Original test case specifications


SYSTEM REQUIREMENTS
═══════════════════════════════════════════════════════════════════════════════
To run this application, you need:

1. Java Development Kit (JDK)
   - Minimum version: JDK 8 or higher
   - Recommended: JDK 11 or later
   - Download: https://www.oracle.com/java/technologies/downloads/

2. Operating System
   - Windows, macOS, or Linux
   - Terminal/Command Prompt with Java support

3. Optional: NetBeans IDE
   - For development and debugging purposes
   - Download: https://netbeans.apache.org/download/


QUICK START GUIDE
═══════════════════════════════════════════════════════════════════════════════

OPTION 1: AUTOMATED EXECUTION (EASIEST - RECOMMENDED)
─────────────────────────────────────────────────────────────────────────────

For PowerShell Users:
    1. Open PowerShell in the project folder
    2. Run: .\run_tests.ps1
    3. View the results automatically

For Command Prompt Users:
    1. Open CMD in the project folder
    2. Run: run_tests.bat
    3. View the results automatically

The script will automatically:
    - Check Java installation
    - Compile the source code
    - Execute all 12 test cases
    - Display color-coded results
    - Generate test_execution_log.txt


OPTION 2: MANUAL EXECUTION
─────────────────────────────────────────────────────────────────────────────

Step 1: Compile
    javac BankingApplicationSimulator.java

Step 2: Run
    java BankingApplicationSimulator


INSTALLATION INSTRUCTIONS
═══════════════════════════════════════════════════════════════════════════════

Step 1: Verify Java Installation
─────────────────────────────────────────────────────────────────────────────
Open a terminal/command prompt and run:
    java -version

You should see output similar to:
    java version "11.0.x" or higher

If Java is not installed:
- Windows: Download JDK from Oracle website and run the installer
- macOS: Use Homebrew: brew install openjdk@11
- Linux: Use package manager: sudo apt-get install default-jdk


Step 2: Download Project Files
─────────────────────────────────────────────────────────────────────────────
Ensure you have all files in the same directory:
- BankingApplicationSimulator.java
- run_tests.ps1
- run_tests.bat
- README.txt


COMPILATION INSTRUCTIONS
═══════════════════════════════════════════════════════════════════════════════

Method 1: Using Automated Scripts (No manual compilation needed)
─────────────────────────────────────────────────────────────────────────────
The scripts handle compilation automatically!


Method 2: Using Command Line
─────────────────────────────────────────────────────────────────────────────
1. Open a terminal/command prompt
2. Navigate to the directory containing BankingApplicationSimulator.java
   Example: cd C:\Users\YourName\Downloads\git

3. Compile the Java file:
   javac BankingApplicationSimulator.java

4. If compilation is successful, you will see new .class files


Method 3: Using NetBeans IDE
─────────────────────────────────────────────────────────────────────────────
1. Open NetBeans IDE
2. Create a new Java Project: File > New Project > Java Application
3. Copy BankingApplicationSimulator.java into the src folder
4. Right-click the file and select "Compile File"


EXECUTION INSTRUCTIONS
═══════════════════════════════════════════════════════════════════════════════

Method 1: AUTOMATED SCRIPT EXECUTION (RECOMMENDED)
─────────────────────────────────────────────────────────────────────────────
The easiest way to run all 12 test cases automatically:

For PowerShell:
    .\run_tests.ps1

For Command Prompt (CMD):
    run_tests.bat

The automated script will:
1. Check Java installation
2. Compile the source code automatically
3. Execute all 12 test cases
4. Display color-coded results
5. Generate a test_execution_log.txt file
6. Show execution summary with pass/fail counts


Method 2: Run from Command Line (Manual)
─────────────────────────────────────────────────────────────────────────────
After successful compilation, run:
    java BankingApplicationSimulator

The application will:
1. Execute 12 automated test cases (4 valid, 8 invalid)
2. Display results for each test case
3. Offer an interactive mode for manual testing


Method 3: Run from NetBeans IDE
─────────────────────────────────────────────────────────────────────────────
1. Right-click BankingApplicationSimulator.java
2. Select "Run File" or press Shift+F6
3. View output in the NetBeans output console


TEST CASES DOCUMENTATION
═══════════════════════════════════════════════════════════════════════════════

The application executes 12 test cases based on Sprint 2 documentation:

VALID TEST CASES (4) - Expected to PASS
─────────────────────────────────────────────────────────────────────────────

TC01: Valid transaction with correct data
      Bank=123, Branch=4567, Account=1234567890, Key=Abc12345, Order=Checkbook
      Expected: Request processed successfully
      Equivalence Class: Valida 1

TC07: Valid request for statement
      Bank=999, Branch=1111, Account=9876543210, Key=ZxY98765, Order=Statement
      Expected: Request processed successfully
      Equivalence Class: Valida 2

TC08: Valid request for checkbook
      Bank=555, Branch=2222, Account=1111111111, Key=Key12345, Order=Checkbook
      Expected: Request processed successfully
      Equivalence Class: Valida 3

TC12: Valid transaction with boundary values (minimums)
      Bank=000, Branch=0000, Account=0000000000, Key=Edge1234, Order=Checkbook
      Expected: Request processed successfully
      Equivalence Class: Valida (Borde) 4


INVALID TEST CASES (8) - Expected to FAIL
─────────────────────────────────────────────────────────────────────────────

TC02: Bank code too short
      Bank=12, Branch=4567, Account=1234567890, Key=Abc12345, Order=Statement
      Expected: Error - invalid bank code
      Equivalence Class: Banco (<3 digitos) 5

TC03: Branch code non-numeric
      Bank=123, Branch=ABCD, Account=1234567890, Key=Abc12345, Order=Checkbook
      Expected: Error - invalid branch code
      Equivalence Class: Sucursal (no numerico) 6

TC04: Account number too long
      Bank=123, Branch=4567, Account=1234567890123, Key=Abc12345, Order=Statement
      Expected: Error - invalid account number
      Equivalence Class: Cuenta (>10 digitos) 7

TC05: Personal key too short
      Bank=123, Branch=4567, Account=1234567890, Key=A1, Order=Checkbook
      Expected: Error - invalid personal key
      Equivalence Class: Clave personal (demasiado corta) 8

TC06: Invalid order type
      Bank=123, Branch=4567, Account=1234567890, Key=Abc12345, Order=Loan
      Expected: Error - invalid order type
      Equivalence Class: Valor de Orden (no Checkbook/Statement) 9

TC09: Bank code non-numeric
      Bank=ABC, Branch=4567, Account=1234567890, Key=Abc12345, Order=Statement
      Expected: Error - invalid bank code
      Equivalence Class: Banco (no numerico) 10

TC10: Branch code too short
      Bank=123, Branch=45, Account=1234567890, Key=Abc12345, Order=Checkbook
      Expected: Error - invalid branch code
      Equivalence Class: Sucursal (<4 digitos) 11

TC11: Account number non-numeric
      Bank=123, Branch=4567, Account=ABCDE12345, Key=Abc12345, Order=Statement
      Expected: Error - invalid account number
      Equivalence Class: Cuenta (no numerico) 12


INTERACTIVE MODE
═══════════════════════════════════════════════════════════════════════════════
After automated tests complete, you can manually test transactions:

1. When prompted, enter 'Y' to process a manual transaction
2. Enter each field when requested:
   - Bank Code (3 digits, numeric: 000-999)
   - Branch Code (4 digits, numeric: 0000-9999)
   - Account Number (10 digits, numeric)
   - Personal Key (min 8 alphanumeric characters)
   - Order Value (Checkbook or Statement)

3. The system will validate and display results
4. You can process multiple transactions or enter 'N' to exit


VALIDATION RULES (EQUIVALENCE CLASSES)
═══════════════════════════════════════════════════════════════════════════════

BANK CODE
─────────────────────────────────────────────────────────────────────────────
Valid Class:        3-digit numeric (000-999)
Invalid Classes:    - Less than 3 digits
                    - More than 3 digits
                    - Contains non-numeric characters
                    - Null or empty


BRANCH CODE
─────────────────────────────────────────────────────────────────────────────
Valid Class:        4-digit numeric (0000-9999)
Invalid Classes:    - Less than 4 digits
                    - More than 4 digits
                    - Contains non-numeric characters
                    - Null or empty


ACCOUNT NUMBER
─────────────────────────────────────────────────────────────────────────────
Valid Class:        10-digit numeric
Invalid Classes:    - Less than 10 digits
                    - More than 10 digits
                    - Contains non-numeric characters
                    - Null or empty


PERSONAL KEY
─────────────────────────────────────────────────────────────────────────────
Valid Class:        Alphanumeric, minimum 8 characters
Invalid Classes:    - Less than 8 characters
                    - Contains special characters
                    - Null or empty


ORDER VALUE
─────────────────────────────────────────────────────────────────────────────
Valid Classes:      "Checkbook" (checkbook request)
                    "Statement" (monthly statement request)
Invalid Classes:    - Any value other than Checkbook or Statement
                    - Case sensitive
                    - Null or empty


OUTPUT INTERPRETATION
═══════════════════════════════════════════════════════════════════════════════

SUCCESS OUTPUT EXAMPLE:
─────────────────────────────────────────────────────────────────────────────
Validation Result:
  Status: SUCCESS
  Request processed successfully
  Confirmation message displayed: Checkbook request approved


ERROR OUTPUT EXAMPLE:
─────────────────────────────────────────────────────────────────────────────
Validation Result:
  Status: FAILURE
  Error: invalid bank code
  Bank code must be exactly 3 digits (received: 2)


AUTOMATED SCRIPT OUTPUT
═══════════════════════════════════════════════════════════════════════════════

When using run_tests.ps1 or run_tests.bat, you will see:

1. Step-by-step progress:
   [Step 1/4] Checking Java installation...
   [Step 2/4] Checking source file...
   [Step 3/4] Compiling Java source code...
   [Step 4/4] Executing automated test cases...

2. Test execution output with color coding (PowerShell):
   - GREEN: Success messages
   - RED: Error messages
   - YELLOW: Test case identifiers
   - CYAN: Section headers

3. Execution summary:
   - Total Test Cases Executed: 12
   - Passed (Valid Cases): 4
   - Failed (Invalid Cases): 8
   - Overall Result: ALL TESTS COMPLETED AS EXPECTED!

4. Log file: test_execution_log.txt
   - Complete execution log
   - Timestamps
   - All test results
   - Saved automatically


TROUBLESHOOTING
═══════════════════════════════════════════════════════════════════════════════

Problem: "javac is not recognized as an internal or external command"
Solution: Java JDK is not installed or not in system PATH
         1. Install JDK from Oracle website
         2. Add JDK bin directory to system PATH environment variable
         3. Restart terminal/command prompt


Problem: "Error: Could not find or load main class BankingApplicationSimulator"
Solution: Class file not found in current directory
         1. Ensure you are in the correct directory
         2. Recompile: javac BankingApplicationSimulator.java
         3. Verify BankingApplicationSimulator.class exists


Problem: "Cannot run script - execution policy"
Solution: PowerShell execution policy blocks scripts
         1. Run PowerShell as Administrator
         2. Execute: Set-ExecutionPolicy RemoteSigned
         3. Or run: powershell -ExecutionPolicy Bypass -File run_tests.ps1


Problem: Colors not displaying in terminal
Solution: Terminal does not support ANSI color codes
         1. Use Windows Terminal or PowerShell 7+
         2. Or use the .bat script instead
         3. Application still works; just without colors


Problem: UnsupportedClassVersionError
Solution: Java version mismatch
         1. Check Java version: java -version
         2. Ensure JDK 8 or higher is installed
         3. Recompile with correct Java version


BEST PRACTICES IDENTIFIED
═══════════════════════════════════════════════════════════════════════════════

1. Input Validation
   - Always validate data type before range validation
   - Check for null/empty values first
   - Use regex for format validation
   - Provide specific error messages for each validation failure

2. Equivalence Class Testing
   - Define clear boundaries for valid and invalid classes
   - Test boundary values (minimum and maximum)
   - Test values within valid ranges
   - Test values outside valid ranges
   - Test special cases (null, empty, non-numeric)

3. Code Organization
   - Separate validation logic for each field
   - Use constants for validation rules
   - Create reusable validation methods
   - Implement clear result reporting

4. User Experience
   - Provide clear, actionable error messages
   - Include both automated and interactive modes
   - Display expected vs. actual values in errors
   - Generate execution logs for reference

5. Automation
   - Use scripts to streamline testing
   - Automate compilation and execution
   - Generate detailed logs
   - Provide color-coded output for clarity


ADDITIONAL NOTES
═══════════════════════════════════════════════════════════════════════════════

- This simulation is for testing and educational purposes only
- No actual banking transactions are performed
- No data is stored or transmitted
- The application runs entirely locally on your machine
- All test cases are executed sequentially for clarity
- Test execution log is saved automatically for review


SUPPORT AND CONTACT
═══════════════════════════════════════════════════════════════════════════════
For questions or issues regarding this application:

Developer:      Jorge Pena
Company:        REM Consultancy
Project:        Testing Procedures for Operational Issues
Sprint:         Sprint 3 - Develop


CHANGELOG
═══════════════════════════════════════════════════════════════════════════════
Version 1.0 (November 2025)
- Initial release
- Implemented 12 test cases from Sprint 2 documentation
- Added equivalence class validation
- Created automated test execution scripts (PowerShell and Batch)
- Integrated color-coded output
- Added execution logging
- Complete documentation


═══════════════════════════════════════════════════════════════════════════════
    END OF README - Thank you for using Banking Application Simulator!
═══════════════════════════════════════════════════════════════════════════════
