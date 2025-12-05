# Frontend Documentation - Compose Multiplatform

Documentacion del frontend con Compose Multiplatform.

## Stack

| Tecnologia | Version | Uso |
|------------|---------|-----|
| Compose Multiplatform | 1.5+ | UI Framework |
| Kotlin | 1.9+ | Lenguaje |
| Voyager | 1.0+ | Navigation |
| Koin | 3.5+ | DI |
| Ktor Client | 2.3+ | HTTP Client |
| Coil | 2.5+ | Image Loading |
| kotlinx.coroutines | 1.7+ | Async |

## Plataformas Soportadas

| Plataforma | Estado | Herramienta |
|------------|--------|-------------|
| Android | ✅ | Android Studio |
| iOS | ✅ | Xcode |
| Desktop | ✅ | IntelliJ IDEA |
| Web | 🔄 | Browser |

## Estructura del Proyecto

```
frontend/
├── shared/                      # Codigo compartido
│   └── src/
│       ├── commonMain/         # Comun a todas las plataformas
│       │   └── kotlin/
│       │       ├── ui/
│       │       │   ├── screens/
│       │       │   ├── components/
│       │       │   └── theme/
│       │       ├── viewmodels/
│       │       ├── repositories/
│       │       └── models/
│       ├── androidMain/        # Android-especifico
│       ├── iosMain/            # iOS-especifico
│       └── desktopMain/        # Desktop-especifico
├── androidApp/                  # App Android
├── iosApp/                      # App iOS (Swift/Xcode)
└── desktopApp/                  # App Desktop
```

## Arquitectura MVI

```
┌─────────────────────────────────────────────────┐
│                     User                         │
└─────────────────────┬───────────────────────────┘
                      │ Interaction
┌─────────────────────▼───────────────────────────┐
│               UI (Composables)                   │
│            collectAsState()                     │
└─────────────────────┬───────────────────────────┘
                      │ Intent/Event
┌─────────────────────▼───────────────────────────┐
│                 ViewModel                        │
│            MutableStateFlow                     │
└─────────────────────┬───────────────────────────┘
                      │ State Update
┌─────────────────────▼───────────────────────────┐
│                State (data class)                │
│          Inmutable, UI deriva de esto           │
└─────────────────────────────────────────────────┘
```

## Documentacion Detallada

| Documento | Ruta |
|-----------|------|
| Componentes | `components/README.md` |
| Design System | `design/material3-theme.md` |
| Pantallas | `screens/index.md` |
| Best Practices | `guides/compose-best-practices.md` |

## Desarrollo Local

### Android

```bash
# Build e instalar
./gradlew :androidApp:installDebug

# Solo build
./gradlew :androidApp:assembleDebug
```

### iOS

```bash
# Generar framework
./gradlew :shared:linkDebugFrameworkIosArm64

# Abrir en Xcode
cd iosApp && pod install && open *.xcworkspace
```

### Desktop

```bash
# Ejecutar
./gradlew :desktopApp:run

# Empaquetar
./gradlew :desktopApp:packageDistributionForCurrentOS
```

## Testing

```bash
# Unit tests
./gradlew :shared:test

# Android instrumented tests
./gradlew :androidApp:connectedAndroidTest

# All tests
./gradlew test
```

## Configuracion de API

```kotlin
// shared/src/commonMain/kotlin/config/ApiConfig.kt
object ApiConfig {
    val baseUrl = when (BuildConfig.FLAVOR) {
        "dev" -> "http://10.0.2.2:8787"  // Android emulator
        "staging" -> "https://api-staging.example.com"
        "prod" -> "https://api.example.com"
        else -> "http://localhost:8787"
    }
}
```
