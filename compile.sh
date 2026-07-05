#!/bin/bash
# compile.sh - Compile the AI Device Login Security System

echo "Compiling AI-Based Device Login Security System..."

mkdir -p out

javac -d out \
  src/com/security/util/PasswordHasher.java \
  src/com/security/util/ConsoleUI.java \
  src/com/security/model/User.java \
  src/com/security/model/LoginAttempt.java \
  src/com/security/model/IntruderAlert.java \
  src/com/security/ai/AIModel.java \
  src/com/security/service/SecurityLog.java \
  src/com/security/service/UserDatabase.java \
  src/com/security/service/SecuritySystem.java \
  src/com/security/LoginSystem.java \
  src/com/security/Main.java

if [ $? -eq 0 ]; then
  echo ""
  echo "Compilation successful!"
  echo "Run with: ./run.sh"
  echo "Or:       java -cp out com.security.Main"
else
  echo ""
  echo "Compilation failed. Please check Java version (requires Java 17+)"
fi
