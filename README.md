# SpendTrack

SpendTrack is an offline-first Android expense tracker built with Kotlin, Jetpack Compose, Room, and Material Design 3. It keeps everything local on the device and focuses on fast expense entry, a clean dashboard, and a compact home screen widget.

## Features

- Monthly dashboard with summary cards, bar chart, pie chart, and compact category totals
- Local-only Room storage with no sign-in or cloud sync
- Add, edit, and delete expense flows
- Compact home screen widget with monthly total, hide/show toggle, quick add, and history shortcuts
- Lightweight quick-add dialog launched from the widget
- Shared validation across the app and widget entry paths

## Tech Stack

- Kotlin
- Jetpack Compose
- Room
- Material Design 3
- Android App Widget APIs

## Project Structure

- `app/src/main/java/com/spendtrack/data` Room entities, DAO, repository, and dashboard summary models
- `app/src/main/java/com/spendtrack/domain` input validation and expense category models
- `app/src/main/java/com/spendtrack/ui` main Compose UI, screens, theme, and app shell
- `app/src/main/java/com/spendtrack/widget` widget provider, widget updater, and quick-add activity
- `app/src/main/res` theme, widget layout, and app resources

## Requirements

- Android Studio
- Android SDK 35
- Java 17

## Build and Run

From the project root:

```bash
./gradlew assembleDebug
```

To install on a connected device or emulator, run the app from Android Studio or use:

```bash
./gradlew installDebug
```

## Widget Behavior

The widget is designed for two rows only:

- Top row: app name, monthly total, and an eye icon to hide or show the amount
- Bottom row: Add Expense and History buttons

The Add Expense button opens a lightweight transparent quick-add dialog instead of the full app. The History button opens the app directly to the history screen.

## Design Notes

- Dark theme only, with no dynamic colors
- Rounded cards and subtle dividers
- WhatsApp-like visual feel with a compact, polished layout
- Monthly summary card, bar chart, pie chart, and compact category cards on the dashboard

## Notes

- No logins
- No servers
- No network dependency for core features
- Widget updates refresh the local monthly total after save, edit, and delete actions
