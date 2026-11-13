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

I split the app into feature and core modules so each screen only depends on what it actually needs. Features can see the domain, models, shared UI and the design system, but never HTTP, JSON or each other. Model, domain, network and data are plain JVM modules, which means most of the logic can be tested without an emulator. The shared Gradle setup lives in `build-logic` convention plugins, so each module's build file stays short.

### State and navigation

Every screen follows the same pattern: the ViewModel exposes one immutable state, and the screen sends typed actions back. Loading, refreshing and errors all use a single `ScreenState` type. That way, if a refresh fails, you keep seeing what was already on screen instead of getting kicked to an error page.

For navigation I went with Navigation 3 and typed destinations. The club or class ID lives in the back-stack key and is handed to the ViewModel through Hilt assisted injection, so a ViewModel never exists without the data it needs. When someone signs out, the whole back stack is replaced, so nothing from the previous account hangs around.

### Networking and auth

Token handling was the part I most wanted to get right.

- The access token is added automatically, but only to our own API's protected endpoints. Login, refresh and any other host never see it.
- The login response tells us when the token expires (`expiresIn`), so the app refreshes it about 30 seconds early instead of waiting for a 401. If a 401 does come back, it refreshes once and replays the request.
- If several requests need a refresh at the same time, only one refresh actually happens. If the server rejects the refresh token, the user is signed out; if it's just a temporary server error, the session is kept.
- Signing out cancels anything still in flight first, so a slow response can't show up under the next person's account.
- Repositories return `Result.Success` or `Result.Failure` rather than throwing. A failure carries a reason, a message and the server's request ID, which is handy for support.
- Reads are retried up to twice (after 500 ms, then 1 s) on network errors, 429s and 5xx. Bookings and cancellations are never retried automatically, because I didn't want to risk booking someone twice.

### Defensive parsing

The home screen is driven by the server, so I assumed the payload would change over time.

- Home blocks are decoded by their `type`. Anything unknown or malformed is skipped, and the rest of the page still renders.
- Timetable dates and times are parsed in the data layer. A bad class or day is dropped; if the week itself can't be read, the screen shows an error with a retry rather than crashing.
- A booking status I don't recognise becomes `UNKNOWN`. It's never treated as "not booked", so the app won't offer to book a class someone may already be in.

## Things I noticed about the API

- **Times are venue times.** I show them exactly as the API sends them, without converting to the phone's timezone. The server's `selectedDate` decides which day opens first, so a Cape Town club and a London club can open on different days at the same moment.
- **The timetable isn't always sorted, and can contain duplicates.** The app sorts classes by start time and removes duplicates.
- **There's no class-details endpoint.** I look the class up in the weekly timetable instead.
- **Class IDs contain `::` and a date.** They have to be encoded as a single path segment, or the URL breaks.
- **Booking and joining the waitlist use the same endpoint.** A class can fill up between viewing it and confirming, so I let the response decide which one actually happened.
- **A booking can time out after the server has already saved it.** After a failed attempt, the app refreshes the class before allowing another try, and double taps are blocked.
- **Late cancellation isn't enforced by the API.** The app warns the user if they cancel within 12 hours of the start. Cancelling returns an empty 204.
- **The login response already includes the user**, so I reuse it and skip an extra `/me` call.
- **`imageRef` values are asset keys, not URLs.** I used text cards rather than guessing at image locations.
- **The API is deliberately slow and sometimes fails**, so every screen has loading, retry and pull-to-refresh states.
- **The reminder opens the calendar app** pre-filled with the class. The user saves it there, so the app never claims the event was saved.

## Testing

There are 73 unit tests. The ones I care most about cover token refresh (concurrent, rejected, temporary failure, early refresh, signing out mid-refresh), read retries, error mapping, malformed payloads, venue time and the 12-hour cancellation boundary. There are also tests for the booking rules, double taps and each screen's ViewModel. The network tests use MockWebServer, so they don't need the mock API running. I also went through the app by hand on an emulator with both accounts, and on my own phone.

## What I'd do next

- Keep people signed in between launches using Keystore-backed token storage. Right now tokens only live in memory, so you sign in again after the app is killed.
- Move UI strings into resources, and properly test TalkBack, large fonts and tablet layouts.
- Add UI and navigation instrumentation tests, and set up CI.
- Talk to the backend team about an idempotent booking endpoint, and where the `imageRef` assets should come from.
- Add telemetry for server content the app had to skip, so we'd know when the payload changes.

## AI usage

- **Where:** I used Claude Code as a pair programmer, mostly to review each module against the brief, implement the fixes I agreed with, and run the app on my phone.
- **What I kept and what I cut:** I treated its suggestions as proposals, not answers. I kept what served the brief (token refresh, error handling, defensive parsing) and cut what didn't: extra abstraction, unused fields and comments, and a README that had grown far too long.
- **With more time:** persistent sessions, instrumentation tests with CI, and an idempotent booking API.
