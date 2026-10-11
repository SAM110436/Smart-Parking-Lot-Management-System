@echo off
REM ========================================================
REM  SMART PARKING LOT MANAGEMENT SYSTEM - RUN SCRIPT
REM ========================================================
cd /d "%~dp0"
chcp 65001 > nul
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
echo [1/2] Compiling Smart Parking Lot Management System...
if not exist "bin" mkdir bin
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
set COMPILE_STATUS=%ERRORLEVEL%
if exist sources.txt del sources.txt

if %COMPILE_STATUS% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %COMPILE_STATUS%
)

echo [2/2] Starting Application...
echo.
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp bin com.smartparking.Main
pause
