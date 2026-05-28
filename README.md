# Detour

An AI-powered Android app that suggests interesting stops along your route — cafes, parks, scenic lookouts, and more — based on your interests and time budget.

## Features

- **Route Setup** — Enter origin, destination, interest categories, and max detour time
- **Map View** — See your baseline route and all suggested stops on a Google Map, color-coded by category
- **Detour Detail** — View AI-generated reasoning, distance/time cost, and rating for each stop

## Screenshots



## Tech Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation |
| Maps | Google Maps SDK + Maps Compose + Maps Utils |
| Networking | Retrofit 2 + OkHttp3 + Gson |
| State | ViewModel + LiveData + Kotlin Coroutines |
| Architecture | MVVM + Repository pattern |

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 26+
- A Google Maps API key
- The [Detour backend](http://192.168.1.3:8000) running locally (or update the base URL)

### Setup

1. Clone the repository:
   ```bash
   git clone <repo-url>
   cd Detour
   ```

2. Add your Google Maps API key to `app/src/main/AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY_HERE" />
   ```

3. Update the backend base URL in `app/src/main/java/com/detour/app/data/RetrofitClient.kt` if needed:
   ```kotlin
   private const val BASE_URL = "http://<your-server-ip>:8000/"
   ```

4. Build and run on a device or emulator (API 26+).

## Project Structure

```
app/src/main/java/com/detour/app/
├── data/
│   └── model/          # DetourRequest, DetourResponse, Detour
├── repository/         # DetourRepository (wraps API calls)
├── viewmodel/          # DetourViewModel (UiState management)
└── ui/
    ├── setup/          # SetupScreen — route & category input
    ├── map/            # MapScreen — route + detour map view
    ├── detail/         # DetailScreen — stop detail & AI insight
    └── navigation/     # Nav graph / route definitions
```

## API

The app communicates with a local backend over HTTP. The primary endpoint:

```
POST /detours
Body: { origin, destination, categories, max_detour_minutes, user_preferences }
Response: { baseline_route: { polyline }, proposed_detours: [ Detour ] }
```

## License

MIT