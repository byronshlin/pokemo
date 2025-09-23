# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Pokemon Android application built with modern Android architecture patterns using Kotlin. The app fetches Pokemon data from an API, stores it locally using Room database, and displays it in a master-detail interface with navigation between a main list view and detailed Pokemon information.

## Architecture

The project follows **MVVM (Model-View-ViewModel)** architecture pattern with the following key components:

- **Activity**: `MainActivity` - Single activity hosting navigation fragments
- **Fragments**: `MainFragment` (Pokemon list), `DetailsFragment` (Pokemon details)
- **ViewModels**: Handle UI logic and state management using LiveData
- **Repository**: `PokemonRoomRepository` - Data layer abstraction
- **Room Database**: Local data persistence with entities and DAOs
- **Network**: `PokemonNetworkDataSource` - API communication using Retrofit
- **Dependency Injection**: Dagger Hilt for dependency management

## Key Technologies

- **Language**: Kotlin with Java 21 target
- **UI**: View Binding, Navigation Component
- **Database**: Room with coroutines support
- **Network**: Retrofit with OkHttp, Gson converter
- **Async**: Coroutines + RxJava3 (mixed approach)
- **DI**: Dagger Hilt
- **Image Loading**: Glide with OkHttp integration
- **Layout**: FlexboxLayout for responsive design

## Common Development Commands

### Build & Test
```bash
# Clean build
./gradlew clean build

# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "com.byronlin.pokemo.viewmodel.DetailViewModelTest"

# Generate test coverage report
./gradlew createDebugCoverageReport
```

### Code Quality
```bash
# Run lint checks
./gradlew lint

# Generate APK
./gradlew assembleDebug
./gradlew assembleRelease
```

## Project Structure

```
app/src/main/java/com/byronlin/pokemo/
├── activity/           # MainActivity
├── adapter/           # RecyclerView adapters
├── datasource/        # Network data sources and API responses
│   ├── api/          # Retrofit API interfaces
│   └── response/     # API response models
├── fragment/          # UI fragments (MainFragment, DetailsFragment)
├── hilt/             # Dagger Hilt modules (AppModule)
├── model/            # UI models and constants
├── repository/       # Repository pattern implementation
├── room/             # Room database components
│   ├── dao/         # Data Access Objects
│   ├── data/        # Room data models
│   └── entity/      # Room entities
├── utils/           # Utility classes
└── viewmodel/       # ViewModels for UI state management
```

## Key Files

- `app/build.gradle.kts` - Main build configuration with all dependencies
- `gradle/libs.versions.toml` - Version catalog for dependency management
- `app/src/main/res/navigation/nav_graph.xml` - Navigation graph defining fragment transitions
- `hilt/AppModule.kt` - Dagger Hilt dependency injection configuration
- `PokemonApplication.kt` - Application class with Hilt annotation

## Development Notes

### Testing Strategy
The project includes comprehensive testing:
- **Unit Tests**: 6 test files covering ViewModels, data sources, and Room database
- **Testing Tools**: JUnit, Mockito, MockK, Robolectric, Coroutines Test, Truth assertions
- **UI Tests**: Espresso for instrumentation tests

### Data Flow
1. **Network**: Pokemon data fetched via Retrofit from external API
2. **Repository**: `PokemonRoomRepository` manages data between network and local storage
3. **Room Database**: Local caching and offline support
4. **ViewModels**: Expose data to UI via LiveData/StateFlow
5. **Fragments**: Display data using View Binding

### Navigation
Uses Navigation Component with Safe Args for type-safe fragment arguments:
- Main fragment shows Pokemon list
- Detail fragment shows individual Pokemon details
- Animated transitions between fragments

### Dependency Management
All dependencies managed through `gradle/libs.versions.toml` version catalog system for consistent versioning across modules.

## Current Configuration

- **Compile SDK**: 36
- **Min SDK**: 29
- **Target SDK**: 34
- **Version**: 1.0 (versionCode: 2)
- **Kotlin**: 2.2.0
- **AGP**: 8.11.1