#!/usr/bin/env bash
# ========================================================
#  SMART PARKING LOT MANAGEMENT SYSTEM - TEST SUITE (BASH)
# ========================================================
set -e
cd "$(dirname "$0")"
echo "[1/2] Compiling Smart Parking Lot Test Suite..."
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name "*.java")

echo "[2/2] Running Automated Unit & Integration Tests..."
echo ""
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp bin com.smartparking.test.ParkingLotTest
