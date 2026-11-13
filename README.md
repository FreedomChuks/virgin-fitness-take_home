# Virgin Active Android

Native Android assessment client built with Kotlin, Jetpack Compose, kotlinx.serialization, Hilt, coroutines, StateFlow and explicit Gradle module boundaries. Open this `android` directory in Android Studio, rather than the parent mock-server package.

## Run

1. Start the supplied mock API from the parent directory (`start.command`). The server must remain running.
2. Open this directory in Android Studio and allow Gradle sync. Use Android Studio's bundled JDK (21), not the machine's JDK 26. Install SDK 37 if prompted.
3. Select `app`, choose an emulator running API 26 or above, and Run. The debug build uses `http://10.0.2.2:8080/`, which reaches the host machine from the Android emulator.
4. Sign in with `avid.runner@virginactive.mock` or `competitive.swimmer@virginactive.mock`, password `password123`. The stray quote following the first email in the original assessment README is a typo.

Command-line build on this Mac:

```sh
./gradlew '-Dorg.gradle.java.home=/Applications/Android Studio.app/Contents/jbr/Contents/Home' :app:assembleDebug
```

The APK is `app/build/outputs/apk/debug/app-debug.apk`. For a USB-connected physical device, use `adb reverse tcp:8080 tcp:8080` and build with `-PapiBaseUrl=http://127.0.0.1:8080/`. For repeated Android Studio runs on a phone, add `apiBaseUrl=http://127.0.0.1:8080/` to the ignored `local.properties` file. The command-line `-PapiBaseUrl` takes precedence; remove the local setting when returning to an emulator. Re-run `adb reverse tcp:8080 tcp:8080` after reconnecting USB. A base URL must end in `/`. HTTP is enabled only in the debug manifest. A release deployment needs an HTTPS API endpoint.

## Architecture

```text
:app                        composition root, navigation, calendar intent
  ├─ :features:login        login ViewModel and Compose screen
  ├─ :features:home         ordered, server-driven home blocks
  ├─ :features:timetable    weekly class list and day filters
  ├─ :features:booking      class details, confirmation, cancellation
  └─ :core:data             repository implementations and typed DTO → domain mapping
       ├─ :core:network     serializable DTOs, typed API, HTTP, token refresh and safe retries
       └─ :core:domain      repository interfaces and booking use cases
            └─ :core:model pure Kotlin values and venue-time rules

Features → :core:domain, :core:model, :core:ui, :core:designsystem
:core:ui → :core:domain, :core:model, :core:designsystem
:core:network has no Android, domain, or feature dependency.
```

Features cannot see concrete repositories, HTTP types, JSON, or other features. The data layer implements domain-owned interfaces. Hilt owns the dependency graph: `@HiltAndroidApp` bootstraps it, `@AndroidEntryPoint` enables the activity, and `@HiltViewModel` plus `hiltViewModel()` creates destination-scoped ViewModels. Navigation 3 route keys carry typed arguments, which Hilt passes to the timetable and booking ViewModels through generated assisted factories. Singleton repository bindings use `@Binds`; providers supply the JSON configuration, clock, IO dispatcher, token store, HTTP client, network data source and domain use cases. Repository constructors use `@Inject`. There is no manual dependency container or hand-written `ViewModelProvider.Factory`. `:core:model`, `:core:domain`, `:core:network`, and `:core:data` are JVM modules, independently testable without a device.

The domain and model modules remain free of Hilt, Android and serialization annotations. The JVM data module uses constructor injection with Dagger/KSP; it has no production JSON dependency.

Use cases contain shared business rules (`BookClassUseCase`) or endpoint orchestration (`GetClassUseCase`). Use cases are injected into the booking ViewModel. Simple repository reads are not wrapped in pass-through use cases. ViewModels expose immutable state, preserve coroutine cancellation, prevent overlapping writes, and are collected with lifecycle awareness.

## Build conventions

`build-logic` is a separate included build registered in `pluginManagement`. It imports the same `gradle/libs.versions.toml` as the main build; dependency and plugin versions have one source of truth. No `allprojects`/`subprojects` mutation is used.

| Convention plugin | Responsibility |
| --- | --- |
| `vactive.android.application` | Application plugin, SDK levels, Java 17 and unit-test dependencies |
| `vactive.android.library` | Android library defaults and unit-test dependencies |
| `vactive.android.compose` | Compose compiler, BOM, UI libraries, previews and debug tooling |
| `vactive.android.hilt` | Hilt and KSP configuration |
| `vactive.android.feature` | Library + Compose + Hilt, lifecycle/coroutines, and the feature-to-core dependencies |
| `vactive.jvm.library` | Kotlin/JVM, Java 17 and unit-test dependencies |
| `vactive.jvm.dagger` | JVM convention plus Dagger constructor processing |
| `vactive.serialization` | Serialization compiler and JSON runtime |
| `vactive.formatting` | Root Spotless configuration and `check` integration |

Feature build files contain their convention plugin and namespace. The app keeps its ID, version, BuildConfig URL and dependency graph explicit. Core modules opt into only the conventions they need. Formatting covers convention sources as well as application sources; generated files and included-build caches remain excluded. Validate the plugins with `./gradlew :build-logic:validatePlugins`.

## Unidirectional data flow

Each feature exposes a read-only `StateFlow` of immutable state and a typed action contract. State flows from ViewModel to route to screen; user actions flow back through `onAction`. Screens have no ViewModel references and cannot call repositories. Route composables collect state with lifecycle awareness, dispatch state-changing actions to ViewModels, and handle navigation/calendar handoffs at the UI boundary. Presentation-only scroll positions and the displayed clock remain local Compose state.

Each feature keeps its route, screen, ViewModel, state and actions at the package root; smaller composables live in a `components/` package and stay `internal` to the module. Shared loading/refresh/error state is the `ScreenState` type in `:core:ui`, with `refreshing()` and `failed()` helpers used by every feature ViewModel.

Login credentials, visibility and loading/errors live in `LoginState`; passwords are never saved to instance state or disk and are cleared on successful sign-in. Timetable selection lives in `TimetableState` as a `LocalDate?` (`null` means all days), survives refresh within the same week, and resets to the venue's selected date when the week changes. Booking confirmation dialogs live in `BookingState`; a confirm action cannot submit unless a corresponding confirmation is pending. Duplicate taps, dismissal and refresh/reconciliation are covered by action-driven tests.

## Navigation 3

The app uses Navigation 3 `NavDisplay` with serializable `NavKey` destinations and a saveable back stack. There are no string routes, URL-encoded arguments, or Navigation 2 `NavController`/`NavHost` dependencies. Entry decorators provide saveable UI state and per-entry ViewModel stores. Hilt assisted injection supplies runtime club/class arguments while keeping feature modules independent of the app's route types.

An authentication transition replaces the entire navigation scope, clearing authenticated entries and ViewModels on sign-out. Configuration changes preserve the current destination. Process death requires sign-in because credentials remain in memory. Back never removes the last entry; repeated tab selections and class taps do not accumulate duplicate destinations.

## Serialization boundary

`core/network/dto` contains `@Serializable` request and response DTOs. DTOs declare only the fields the client reads; unknown fields are ignored. `RetrofitService` declares suspend endpoints with `@GET`, `@POST`, `@DELETE`, `@Path`, `@Query`, and typed request bodies. Retrofit 3 uses the kotlinx.serialization converter and the Hilt-provided OkHttp client. `NetworkDataSource` returns typed DTOs; repositories map them to pure domain models, parsing timetable dates into `LocalDate` and class times into `OffsetDateTime`. The `safeApiCall` helper turns non-2xx responses into `ApiException` (status, message, request ID); an empty or invalid body becomes `ServerResponseException`. Empty cancellation responses are handled as `Response<Unit>`.

Session state lives in `TokenStore` in `:core:network`: in-memory tokens with their expiry, an observable `signedIn` flow and a single lock shared by login, logout and refresh. `AuthRepositoryImpl` calls `network.login`, saves the tokens and the member returned with the login response, and clears both on logout. `MemberCache` reuses that member so `/me` is fetched only when the login response did not include a readable user. The network layer has no dependency on the data, domain or feature modules.

Home blocks and actions are sealed DTO hierarchies. Their custom serializers inspect only the `type` discriminator and delegate payload decoding to generated serializers. Unknown or malformed optional blocks/actions become explicit `Unknown` values. This keeps future server features from breaking known content. Required class fields such as availability remain required, so malformed data cannot silently become a full or bookable class. Unknown status strings map to safe domain `UNKNOWN` states.

## Design system

`:core:designsystem` owns the Material 3 light/dark palettes, a semantic status palette (success, waitlist, unavailable, error, info, exposed through `ActiveTheme.status`), the type scale, shapes, a 4dp spacing scale (`ActiveSpace`) and shared sizes (`ActiveSize`). Brand red `#C90035` is reserved for brand, selection and primary actions; dark mode uses a brighter red with dark text so contrast holds. Status is always stated in text and reinforced by tone and icon. Cards are flat with hairline outlines: actionable cards are outlined with a chevron and a full-card touch target, read-only content uses a tonal panel without ripple. Tokens (shapes, spacing, sizes and typography) live in `ActiveTokens.kt`, colours in `ActiveColors.kt` and `ActiveStatusColors.kt`. Components: `ActiveButtons.kt` (`ActiveButton`, `ActiveSecondaryButton`, `ActiveTextButton`), `ActiveTextField`, `ActiveCard`, `ActiveBadge`, `ActiveNotice`, `ActiveHero`, `ActiveDateChip`, `ActiveInfoRow`, `ActiveEmptyState`, `ActiveBrandMark` (the launcher glyph) and the `ActiveIcons` set. Buttons are at least 56dp high and other controls at least 48dp. `ActiveTheme.kt` only wires the theme. The combined `ThemePreview.kt` gallery lives in the debug source set.

Feature screen previews live in each feature's `src/debug` source set and use debug-only `PreviewData` from `:core:ui`. They cover login/error/loading, home/empty, timetable/empty day, class details, booked/waitlisted confirmations and reconciliation errors. Open the relevant `*ScreenPreviews.kt` file in Android Studio with the debug variant selected; no Hilt graph or mock API is needed. Core UI previews also cover class cards and loading/error states.

Domain-aware components such as a class card belong in `:core:ui`; the design system itself knows nothing about bookings, repositories, or navigation. Server copy is displayed in server order. Unknown sections/actions are ignored safely. Optional malformed home blocks do not remove valid neighbouring blocks.

## Behaviour and API findings

- Access tokens have a 300-second lifetime. The expiry is computed from `expiresIn`, and a token within 30 seconds of expiry is refreshed before the request is sent. A 401 still triggers one refresh and replay as a fallback. Refresh runs under the token-store lock, so concurrent requests rotate the refresh token once. A rejected refresh (`401 InvalidRefreshToken`) clears the session; temporary server failures preserve it.
- Sign-out cancels in-flight calls before taking the session lock, so it never waits behind a slow refresh, and an abandoned request cannot replay under the next account.
- The login response includes the user. It is decoded leniently and cached, saving a `/me` call; a missing or malformed user falls back to `/me`.
- Credentials are held in memory, never logged or written to preferences. Process death requires sign-in again. This is an explicit assessment trade-off, not persistent authentication.
- Safe GET requests retry up to twice (after 500 ms, then 1 s) for network failures, 429 and server failures. Mutations are not automatically retried on transport errors or 5xx responses.
- A booking timeout may occur after a server-side commit. The UI blocks another write until class status has been refreshed. Duplicate taps cannot submit overlapping bookings.
- Booking and waitlisting share an endpoint with no body. The server's returned booking and class state determine confirmation, including waitlist position. A previously available class can become full before confirmation.
- Class IDs contain `::` and a date. They are encoded as path segments; home actions provide the actual club/class identifiers.
- There is no class-details endpoint; details are resolved from the current weekly timetable. Stale references receive an explicit error.
- The documented schemas contain unresolved class-list and manifest-block references. Actual payloads supply the fields used by this client. `imageRef` values are asset keys, not image URLs; the current UI uses text cards rather than inventing remote image locations.
- Timetable classes are not consistently sorted. The mapper sorts within each day by start instant and drops duplicate days, duplicate class IDs, classes with unreadable times and days with unreadable dates. An unreadable week date fails the timetable with a retryable error instead of crashing the screen. Available classes in the past remain visible but cannot be booked.
- Dates and times are displayed from `OffsetDateTime` without converting to the phone timezone. The API's selected venue date controls the initial day. Cape Town and London can have different selected dates at the same instant.
- Cancellation within 12 hours displays a forfeiture warning. Cancellation returns an empty 204 body. Returning to home or timetable reloads server state.
- Calendar insertion opens the installed calendar app with the class title, start/end instants, and venue timezone. The user saves the event and selects their reminder there. The client does not claim an event has been saved merely because the calendar opened. Devices without a calendar app get a message.
- The API has deliberate multi-second latency. Each screen exposes loading, retry and error states. Server request IDs are retained in error messages for support.
- Home, timetable and class details support Material 3 pull-to-refresh, with a TalkBack refresh action. Existing content remains visible during refresh and after a failed refresh. Home tiles with server actions and timetable class cards are whole-card touch targets. Class details uses an accessible, RTL-aware back arrow.
- Class status ("In progress", "Finished") is computed against a clock that ticks every 30 seconds, so open screens stay current.

## Code formatting

Spotless runs the pinned ktfmt formatter in Kotlin style across Kotlin source/tests and Gradle Kotlin scripts in every module. Generated code and build/cache directories are excluded. `.editorconfig` supplies matching editor defaults; Spotless is the authoritative formatter. It also checks trailing whitespace and final newlines in the README, version catalog, Gradle properties and XML resources.

From this directory, with Android Studio's bundled JDK selected:

```sh
./gradlew spotlessApply   # Format existing code
./gradlew spotlessCheck   # Check without changing files
```

On this Mac, add `'-Dorg.gradle.java.home=/Applications/Android Studio.app/Contents/jbr/Contents/Home'` if your terminal defaults to JDK 26. The root `check` task includes `spotlessCheck`. Android Studio can run these tasks from the Gradle tool window. No CI workflow or Git hook is installed.

## Verification

```sh
./gradlew '-Dorg.gradle.java.home=/Applications/Android Studio.app/Contents/jbr/Contents/Home' \
  :core:model:test :core:domain:test :core:data:test :core:network:test \
  :core:ui:testDebugUnitTest :features:booking:testDebugUnitTest :features:login:testDebugUnitTest \
  :features:timetable:testDebugUnitTest :features:home:testDebugUnitTest :app:testDebugUnitTest \
  :app:assembleDebug :app:lintDebug spotlessCheck
```

The debug APK builds and all 73 unit tests pass. Lint reports zero errors. Manual API 36.1 emulator checks passed for both accounts, server-driven home rendering, timetable navigation, booking, waitlisting, cancellation and the late-cancellation warning. All test bookings were cancelled. Navigation 3 was additionally verified on the emulator for typed class arguments, rotation restoration, system Back, tab selection and clearing the protected stack on sign-out. Google Calendar accepted the insert intent but opened account setup, so event saving and reminder delivery were not verified. The app was also run on a physical Pixel against the local mock (via `adb reverse`): sign-in, home rendering, timetable in venue time and booking confirmation were checked.

Tests also decode captured payloads from both accounts through generated serializers and domain mappers, verify escaped credential round trips and reject missing required availability. Tests cover venue-time rendering, timetable date parsing, proactive refresh, sign-out during a slow refresh, back-stack reuse of the timetable, the twelve-hour boundary, forward-compatible home mapping, booking policy, concurrent refresh, rejected and transient refresh failures, bounded read retries, no mutation retries, duplicate taps, uncertain booking reconciliation, and cancellation followed by a failed refresh. MockWebServer tests use loopback sockets; they do not require the assessment API. Lint's dependency-update warnings are informational; versions are pinned for reproducibility.

## Remaining work before production

- Persist sessions through a reviewed Keystore-backed credential store with explicit expiry/revocation handling if persistent sign-in is required.
- Extract local UI strings into Android resources and validate TalkBack, large fonts, RTL and tablet layouts systematically.
- Add automated Compose navigation/instrumentation coverage, calendar-provider integration checks, and CI against supported Android versions.
- Add the real image assets corresponding to server `imageRef` values, plus a documented registry/fallback strategy.
- Agree API contracts for idempotent booking writes, non-current weeks, cancellation of calendar events, and asset delivery.
- Add privacy-reviewed telemetry for unknown/malformed blocks rather than silently dropping optional content; never log tokens or credentials.

## Repository results

Repository and use-case contracts return the domain `Result<T>`: `Success(data)` or `Failure(AppFailure)`. `AppFailure` is a plain value retaining reason, message, optional HTTP code, and request ID. ViewModels handle both branches explicitly rather than catching expected API errors. Business validation also returns a typed failure. `flatMap` chains dependent operations without running the next step after failure.

The data-layer `mapped` helper translates exceptions into that result: `ApiException` by status, `ServerResponseException` as a server failure, other `IOException`s as network failures, and unreadable payloads (serialization or date parsing) as a data failure. Cancellation is rethrown and unexpected exceptions are not swallowed. Reads opt into retries (`mapped(retry = true)`); writes never retry.

## Automatic HTTP authentication

The Hilt-provided client registers an `Accept: application/json` interceptor, `AuthInterceptor` and `TokenAuthenticator`, sharing one `TokenRefresher`. Repository calls are ordinary `network.member()` / `network.book(...)` calls, with no token parameters.

The interceptor attaches the current token only to protected endpoints on the configured API origin; login, refresh and other origins receive no Authorization header. A signed-out request is answered locally with a 401 and never reaches the server. If the token is about to expire, the interceptor refreshes it first; if that refresh fails transiently it sends the current token and lets the 401 path recover.

The authenticator reuses credentials already refreshed by another request and limits each request to one replay. A second 401 clears the session only if it was rejected with the current token. Refresh uses the base client without the auth interceptor or authenticator, avoiding recursion, and is bounded by the client's call timeout.
