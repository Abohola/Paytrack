# Paytrack project context

## Purpose and success criteria

Paytrack is a private, offline Android expense tracker. A successful release lets a user quickly save an amount, description, category, date, and time; review and edit history; see lightweight spending insights; and export any chosen date range as an Excel-friendly UTF-8 CSV.

## Architecture and important files

- Single Android application module with layered packages: `domain`, `data`, and `ui`.
- Kotlin + Jetpack Compose for UI.
- Android SQLite via a repository abstraction for durable offline storage (`data/ExpenseDatabase.kt`, `data/SqliteExpenseRepository.kt`).
- Android Storage Access Framework for user-selected CSV export destinations.
- No login, network permission, analytics, or cloud dependency.
- `MainActivity.kt` owns app navigation and system file export. `ui/screens` contains dashboard, history, and export views; `ui/components` contains reusable glass and expense controls.

## Commands

- Build debug APK: `./gradlew assembleDebug`
- Unit tests: `./gradlew testDebugUnitTest`
- Android lint: `./gradlew lintDebug`
- Release APK: `./gradlew assembleRelease`

Verified on Windows with JDK 17 and Android SDK 36 on 2026-08-30:

- `./gradlew testDebugUnitTest` — passed.
- `./gradlew lintDebug` — passed with no errors; remaining warnings are SDK/dependency update notices constrained by the installed SDK.
- `./gradlew assembleRelease` — passed.
- `apksigner verify --verbose --print-certs` — v1 and v2 signatures verified.
- APK manifest inspection confirms min SDK 23, target SDK 36, and no internet permission.

## Decisions

- CSV was chosen over native XLSX because it is universally readable by Excel, transparent, small, and avoids a heavy spreadsheet dependency. The export includes a UTF-8 BOM, stable decimal formatting, and explicit currency code.
- Money is stored as integer minor units to avoid floating-point rounding errors.
- The visual language adapts liquid-glass principles to Android using translucent surfaces, subtle borders, depth, and restrained motion over a crimson/noir palette.
- The v1 release is signed with the local Android debug certificate for direct sideloading. Use a protected production keystore before Play Store distribution.

## Current status

Version 1.0.0 is feature-complete and built on 2026-08-30. The local release artifact is `release/Paytrack-1.0.0.apk` (SHA-256 `DD811E18F33A58FD4D34985D2E6B1F9C7203CB5FA16935E9EF5D952E03EFFC5B`, 12,599,520 bytes).

Implemented: expense add/edit/delete, seven categories, current timestamp capture, monthly total, daily-average and largest-expense insights, history search and filters, custom/preset export ranges, Excel-safe UTF-8 CSV, and a themed adaptive wallet icon.

Known limitation: no Android device or emulator was connected during this run, so validation covered compilation, unit tests, lint, APK metadata, permissions, and signing rather than an interactive device smoke test. GitHub publication is pending valid GitHub CLI authentication.
