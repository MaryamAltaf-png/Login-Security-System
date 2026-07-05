@echo off
REM Optional CLI version (text-based) — opens a console window.
title AI Login Security System (CLI)
color 0A

where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Java is not installed. Get it from https://adoptium.net/
    pause
    exit /b 1
)

cd /d "%~dp0"
java -jar AILoginSecuritySystem-CLI.jar

echo.
echo Press any key to close...
pause >nul
