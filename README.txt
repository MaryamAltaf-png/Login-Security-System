================================================================
  AI-BASED DEVICE LOGIN SECURITY SYSTEM
  Java OOP Project with OpenAI Integration
================================================================

OVERVIEW
--------
A complete security login system built in Java using Object-Oriented
Programming principles. Features AI-powered threat analysis via OpenAI.


OOP CONCEPTS DEMONSTRATED
--------------------------
1. ENCAPSULATION
   - All fields are private in every class
   - Controlled access through getters/setters
   - Example: User.java, LoginAttempt.java

2. ABSTRACTION
   - AIModel hides HTTP API complexity behind simple method calls:
     analyzeIntruderThreat(), analyzeLoginPattern()
   - Users of AIModel don't need to know how OpenAI is called

3. COMPOSITION
   - SecuritySystem is composed of: UserDatabase + SecurityLog + AIModel
   - LoginSystem is composed of SecuritySystem + Scanner
   - No inheritance needed - preferred "has-a" over "is-a"

4. CONSTRUCTOR OVERLOADING
   - User(username, password) - creates USER role by default
   - User(username, password, role) - creates with specified role

5. IMMUTABILITY
   - LoginAttempt is immutable (all fields final, set in constructor)

6. UTILITY CLASSES (Static factory pattern)
   - PasswordHasher - static hash() and verify() methods
   - ConsoleUI - static print helpers with color formatting


PROJECT STRUCTURE
-----------------
src/com/security/
├── Main.java                    - Entry point
├── LoginSystem.java             - Interactive CLI menu
├── model/
│   ├── User.java                - User entity with OOP encapsulation
│   ├── LoginAttempt.java        - Immutable login event record
│   └── IntruderAlert.java      - Intruder detection event
├── service/
│   ├── SecuritySystem.java     - Core authentication engine
│   ├── UserDatabase.java        - In-memory user registry
│   └── SecurityLog.java        - File-based activity logger
├── ai/
│   └── AIModel.java            - OpenAI integration for threat analysis
└── util/
    ├── PasswordHasher.java     - SHA-256 + salt password hashing
    └── ConsoleUI.java          - Colored terminal output


REQUIREMENTS
------------
- Java 17 or higher (uses switch expressions, text blocks)
- OpenAI API key (optional - system works in offline mode without it)


HOW TO COMPILE
--------------
  Option A (script):
    bash compile.sh

  Option B (manual):
    mkdir -p out
    javac -d out src/com/security/**/*.java src/com/security/*.java


HOW TO RUN
----------
  Option A (script):
    bash run.sh

  Option B (with AI):
    export OPENAI_API_KEY=sk-your-key-here
    java -cp out com.security.Main

  Option C (offline mode - no API key needed):
    java -cp out com.security.Main


DEFAULT TEST ACCOUNTS
---------------------
  Username: admin    Password: Admin@1234    Role: ADMIN
  Username: alice    Password: Alice@Pass1   Role: USER
  Username: bob      Password: Bob@Secure2   Role: USER


PASSWORD POLICY
---------------
  - Minimum 8 characters
  - At least 1 uppercase letter
  - At least 1 digit
  - Stored as SHA-256 hash with random salt


SECURITY FEATURES
-----------------
  ✔ Password hashing (SHA-256 + random salt)
  ✔ Account lockout after 3 failed attempts
  ✔ Intruder detection and alert generation
  ✔ AI-powered threat analysis (online or offline)
  ✔ Login event logging to logs/login_attempts.txt
  ✔ Intruder reports saved to intruder_logs/
  ✔ Session management


AI FEATURES (when OPENAI_API_KEY is set)
-----------------------------------------
  - Intruder threat classification (LOW/MEDIUM/HIGH/CRITICAL)
  - Login pattern anomaly detection
  - Security dashboard recommendations
  - Countermeasure suggestions


ARCHITECTURE DIAGRAM
---------------------

  User (CLI)
      ↓
  LoginSystem  ──────────────────────────┐
      ↓                                  │
  SecuritySystem (authentication core)  │
      ├── UserDatabase (user registry)  │
      ├── SecurityLog (file logging)    │
      └── AIModel (OpenAI integration)  │
              ↓                          │
         OpenAI API                     │
         (online) or                    │
         offline fallback               │
              ↓                          │
         Analysis returned ────────────┘
              ↓
        Console output + log files


================================================================
  Semester Project | Java OOP | AI Integration
================================================================
