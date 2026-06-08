# One Thing — Android

## Project overview
"One Thing" is a minimalist daily focus app. Users set a single task per day and mark it complete. Core pillars: bold typography, satisfying completion animation, streak mechanic.

## Tech stack
- **Language**: Kotlin
- **UI**: Jetpack Compose + Material3
- **Persistence**: Room (SQLite)
- **DI**: Hilt
- **Navigation**: Compose Navigation
- **Min SDK**: 26 (Android 8.0) — use `java.time.LocalDate` freely

## Build & run
```bash
./gradlew assembleDebug          # build
./gradlew installDebug           # install to connected device/emulator
./gradlew test                   # unit tests (StreakCalculatorTest)
./gradlew connectedAndroidTest   # instrumented tests
```

## Project structure
```
app/src/main/java/dev/harryakbar/onething/
├── OneThingApp.kt           # @HiltAndroidApp Application
├── MainActivity.kt          # @AndroidEntryPoint, hosts NavHost
├── data/
│   ├── DailyTask.kt         # Room @Entity — date (PK), title, isCompleted, completedAt
│   ├── DailyTaskDao.kt      # DAO — getByDate, getAll, upsert
│   └── AppDatabase.kt       # RoomDatabase singleton
├── repository/
│   └── TaskRepository.kt    # Single source of truth, injected via Hilt
├── ui/
│   ├── theme/Theme.kt       # Dark-only palette: Background=#0D0D0D, Accent=#E8A838
│   ├── today/               # TodayScreen + TodayViewModel (empty / set states)
│   ├── complete/            # CompleteScreen + CompleteViewModel (streak celebration)
│   └── streak/              # StreakScreen + StreakViewModel (30-day grid)
└── utils/
    └── StreakCalculator.kt  # Pure function: List<DailyTask> → Int streak
```

## Key design decisions
1. **One task per day**: `date` ("yyyy-MM-dd") is the Room primary key — upsert enforces uniqueness.
2. **Date handling**: Always use `LocalDate.now()` (device local timezone). Store as ISO string.
3. **Completion animation**: Full-screen color fill from tap point (~600ms), then navigate to CompleteScreen.
4. **Haptic feedback**: `VibrationEffect.EFFECT_HEAVY_CLICK` on completion tap.
5. **Dark-only**: No light theme for v1. The dark aesthetic IS the brand.
6. **Navigation routes**: `"today"`, `"complete/{streak}"`, `"streak"`.

## Streak logic
`StreakCalculator.calculate(tasks, today)`:
- Sort completed tasks descending by date.
- Count consecutive days ending at today or yesterday (gap of one day for "just completed" case).
- Returns 0 if no completed tasks or streak is broken.

## Adding features
- New screens: add route to `NavHost` in `MainActivity`, create Screen + ViewModel pair.
- New data fields: add column to `DailyTask`, bump Room `version`, add `Migration`.
- Tests: `StreakCalculatorTest` in `src/test/` — pure JVM, no Android dependencies needed.
