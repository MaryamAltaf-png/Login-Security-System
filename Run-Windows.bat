@echo off
REM AI-Based Device Login Security System - Windows Launcher (GUI)
REM Just double-click this file to run.

where javaw >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo.
    echo  =========================================================
    echo   ERROR: Java is not installed or not in PATH
    echo  =========================================================
    echo.
    echo  Please install Java 17 or higher from:
    echo    https://adoptium.net/   (recommended, free)
    echo  or
    echo    https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

REM Real OpenAI analysis enabled (gpt-4o-mini)
set OPENAI_API_KEY=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

cd /d "%~dp0"
start "" javaw -jar "AILoginSecuritySystem.jar"
exit /b 0
