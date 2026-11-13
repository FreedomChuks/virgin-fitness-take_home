# Virgin Active Android

Native Android client for the Virgin Active mock API, built with Kotlin, Jetpack Compose, Hilt, Navigation 3, Retrofit and kotlinx.serialization.

## Run

1. Start the mock API from the parent directory (`start.command`) and leave it running.
2. Open this `android` directory in Android Studio, using its bundled JDK 21. Install SDK 37 if prompted.
3. Run `app` on an emulator (API 26+). The debug build points at `http://10.0.2.2:8080/`.
4. Sign in with `avid.runner@virginactive.mock` or `competitive.swimmer@virginactive.mock`, password `password123`.

On a USB-connected phone, run `adb reverse tcp:8080 tcp:8080` and add `apiBaseUrl=http://127.0.0.1:8080/` to `local.properties` (or pass `-PapiBaseUrl=...`). HTTP is only allowed in debug builds.

Tests, lint and formatting:

```sh
./gradlew test lintDebug spotlessCheck
```

## Approach

### Modules

```text
:app                  Hilt wiring, Navigation 3, calendar intent
:features:*           login, home, timetable, booking (screen, ViewModel, components/)
:core:ui              class card, ScreenState, date/time formatting
:core:designsystem    theme, tokens, shared components
:core:data            repositories, DTO → model mapping, error mapping, retries
:core:network         Retrofit API, DTOs, token handling
:core:domain          Result type, repository interfaces, booking use cases
:core:model           plain Kotlin models and venue-time rules
```

Features only see `:core:domain`, `:core:model`, `:core:ui` and `:core:designsystem`, never HTTP, JSON or each other. Model, domain, network and data are plain JVM modules, so most logic is tested without a device. Shared Gradle setup lives in `build-logic` convention plugins.

### State and navigation

Each screen has an immutable state `StateFlow` and a typed action interface: state flows down, actions flow up. Loading, refreshing and errors share one `ScreenState` type, so every screen keeps showing its content during a refresh or after a failed one.

Navigation 3 uses typed, serializable destinations. Club and class IDs travel in the back-stack key and reach the ViewModel through Hilt assisted injection, so a ViewModel always has its arguments from creation. Signing out replaces the whole back stack, so nothing from the previous account survives.

### Networking and auth

- The token is attached automatically, only to our API's protected endpoints, never to login, refresh or other hosts.
- Tokens are refreshed 30 seconds before they expire, based on `expiresIn`. A 401 triggers one refresh and replay as a fallback.
- Concurrent requests share a single refresh. A rejected refresh signs the user out; a temporary server failure keeps the session.
- Sign-out cancels in-flight requests first, so a late response can't land under the next account.
- Repositories return `Result.Success` or `Result.Failure` instead of throwing. Failures carry a reason, message and the server's request ID.
- Reads retry up to twice (500 ms, then 1 s) on network errors, 429 and 5xx. Bookings and cancellations are never retried automatically.

### Defensive parsing

- Home blocks are decoded by their `type`. Unknown or malformed blocks are dropped without affecting the rest of the page, so new server features don't break the app.
- Timetable dates and times are parsed in the data layer. Bad classes or days are dropped; an unreadable week shows a retryable error.
- Unknown booking statuses map to `UNKNOWN`, never to "not booked".

## API observations

- **Venue time:** times are shown exactly as the API sends them, in the club's timezone. The server's `selectedDate` picks the initial day, so Cape Town and London can start on different days.
- **Sorting:** timetable classes aren't consistently sorted or unique. The client sorts by start time and removes duplicates.
- **No class-details endpoint:** class details are looked up from the weekly timetable.
- **Class IDs:** they contain `::` and a date, and are encoded as a single path segment.
- **Booking vs waitlist:** the same endpoint handles both. The response decides which one happened, because a class can fill up between viewing and confirming.
- **Timed-out bookings:** a booking can time out after the server has already saved it. The app refreshes the class status before allowing another attempt, and blocks double taps.
- **Late cancellation:** within 12 hours of the start, the app warns about forfeiting the cost; the API doesn't enforce this. Cancellation returns an empty 204.
- **User in the login response:** it's reused, which saves a `/me` call.
- **`imageRef`:** these values are asset keys, not URLs, so the UI uses text cards.
- **Latency:** the API is deliberately slow and sometimes fails. Every screen has loading, retry and pull-to-refresh states.
- **Calendar reminder:** the reminder opens the calendar app pre-filled with the class. The user saves it there, and the app doesn't claim it was saved.

## Testing

73 unit tests cover token refresh (concurrent, rejected, transient, proactive, sign-out mid-refresh), read retries, error mapping, malformed payloads, venue time and the 12-hour boundary, booking rules, duplicate taps and the screen ViewModels. Network tests use MockWebServer and don't need the mock API running. The app was also checked by hand on an emulator and a physical device with both accounts.

## Next steps

- Keep users signed in with Keystore-backed token storage (tokens are currently held in memory only).
- Move UI strings to resources, and test TalkBack, large fonts and tablet layouts.
- Add UI and navigation instrumentation tests, plus CI.
- Agree an idempotent booking API with the backend, and a source for `imageRef` assets.
- Add telemetry for dropped or malformed server content.

## AI usage

- **Where:** I used Claude Code as a pair programmer, mainly to review each module against the brief, implement the fixes I approved, and run the app on a device.
- **Accepted vs rejected:** I treated its suggestions as proposals. I kept what served the brief (token refresh, error handling, defensive parsing) and cut what didn't: extra abstraction, unused fields and comments, an over-long README.
- **With more time:** persistent sessions, instrumentation tests with CI, and an idempotent booking API.
