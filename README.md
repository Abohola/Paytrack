# Paytrack

Paytrack is a simple, private Android expense tracker with a crimson liquid-glass interface.

## Download

[Download Paytrack 1.0.0 APK](https://github.com/Abohola/Paytrack/releases/download/v1.0.0/Paytrack-1.0.0.apk) (Android 6.0 or newer)

## Features

- Fast expense entry with amount, description, category, date, and time
- Offline SQLite storage with no account and no internet permission
- Dashboard totals and practical spending insights
- Search, edit, and delete expense history
- Custom date-range export to Excel-friendly UTF-8 CSV
- Device-locale currency formatting

## Build

Requires JDK 17 and Android SDK 36.

```bash
./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.
