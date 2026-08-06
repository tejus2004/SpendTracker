# SpendTrack Copilot Instructions

- Keep changes offline-first and local-only.
- Prefer Kotlin, Jetpack Compose, Room, and Material Design 3.
- Preserve the widget quick-add flow and refresh widget totals after save, edit, and delete operations.
- Keep validation in the shared expense input path so the widget and app screens behave the same.
- Avoid introducing server, login, or sync dependencies.
- Keep UI clean, modern, and intentional rather than generic.
- When editing history or dashboard flows, keep them driven by the repository flows rather than one-off state.
- If you add new expense categories, update the domain model, widget shortcuts, and UI selectors together.
