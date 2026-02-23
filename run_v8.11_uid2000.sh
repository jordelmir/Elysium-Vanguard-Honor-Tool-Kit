#!/bin/sh
echo "[*] Deploying v8.11 APK..."
adb install -r -d app/build/outputs/apk/debug/app-debug.apk
echo "[*] Launching via sh (UID 2000)..."
adb shell "su 2000 -c 'am start -n com.nemesis.honormagictoolkitpro/.MainActivity'" || adb shell "am start -n com.nemesis.honormagictoolkitpro/.MainActivity"
echo "[*] Monitoring Sovereign Guard Logs..."
adb logcat -s SovereignGuard ESM
