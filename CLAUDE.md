# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Session Instructions

De ahora en adelante, actúa como mi asistente experto, con acceso a todo tu razonamiento y conocimiento. Siempre proporciona:
- Una respuesta clara y directa a mi solicitud.
- Una explicación paso a paso de cómo llegaste allí.
- Perspectivas o soluciones alternativas que tal vez no se me hayan ocurrido.
- Un resumen práctico o un plan de acción que pueda aplicar de inmediato.

Nunca des respuestas vagas. Si la pregunta es amplia, divídela en partes. Si te pido ayuda, actúa como un profesional en ese ámbito (profesor, entrenador, ingeniero, médico, etc.). Lleva tu razonamiento al 100% de tu capacidad.

## Project Overview

Party Gallery is a Kotlin Multiplatform social party app for capturing, sharing, and discovering party moments. The project targets Android, iOS, Web, and Desktop platforms using Compose Multiplatform.

**Current Status**: Active development (~35% global progress)

| Layer | Status | Details |
|-------|--------|---------|
| **Domain** | 100% | 7 models, 7 repository interfaces |
| **Data** | 17% | AuthRepositoryImpl done, 6 pending |
| **Presentation** | 82% | 6/9 stores, 11/15 screens, 9/9 components |
| **Testing** | 0% | Infrastructure pending |

**Platforms Working:**
- Desktop: Compose Desktop ✓
- Web: Compose HTML ✓ (Dark Mode First)
- iOS: Compose Multiplatform ✓ (dynamic framework)
- Android: Entry point ready (requires emulator)

## Architecture

The project follows Clean Architecture with MVI (Model-View-Intent) pattern:

```
shared/commonMain/kotlin/com/app/
├── presentation/     # UI & State Management (Compose Multiplatform)
│   ├── ui/           # Components, screens, theme, navigation
│   ├── intent/       # User intents/actions
│   ├── state/        # UI states and state reducers
│   └── store/        # State stores and state management
├── domain/           # Business Logic & Use Cases
│   ├── model/        # Domain models
│   ├── usecase/      # Use cases
│   ├── repository/   # Repository interfaces
│   └── validation/   # Business rules
├── data/             # Repositories & Data Sources
│   ├── repository/   # Repository implementations
│   ├── datasource/   # Local, remote, social integrations
│   ├── mapper/       # Data mappers
│   └── cache/        # Caching strategies
└── di/               # Dependency Injection modules
```

**Key Patterns:**
- Repository pattern for data layer abstraction
- Use Cases for single-responsibility business logic
- MVI State Stores for unidirectional data flow
- Intent-based user actions with state reducers
- Koin for dependency injection with modular setup
- Coroutines + Flow for async operations
- Voyager for multiplatform navigation

## Design System - Dark Mode First

Party Gallery utiliza un diseño **Dark Mode First** con acentos Amber que:
- Resalta el contenido visual (fotos/videos)
- Reduce fatiga visual en ambientes de fiesta
- Crea una estetica premium y moderna
- Optimiza para visualizacion nocturna

### Color Palette

| Token | Valor | Uso |
|-------|-------|-----|
| `background` | #0A0A0A | Fondo principal |
| `surface` | #141414 | Cards base |
| `surfaceVariant` | #1E1E1E | Cards elevadas, inputs |
| `primary` | #F59E0B | Acciones, acentos (Amber) |
| `secondary` | #FBBF24 | Badges, secundario |
| `onSurface` | #FFFFFF | Texto principal |
| `onSurfaceVariant` | #A1A1AA | Texto secundario |
| `outline` | #3F3F46 | Bordes |
| `error` | #EF4444 | Errores, Live indicator |

### Core Components

- **MediaCard**: Card con imagen full-bleed y gradient overlay
- **Avatar**: Circular con borde gradient opcional (Amber)
- **PillTabs**: Tabs estilo pill con seleccion
- **BottomNavBar**: Navegacion con amber activo
- **LiveBadge**: Badge rojo pulsante
- **MoodTag**: Chip con color por mood (HYPE, CHILL, WILD, ROMANTIC, CRAZY, ELEGANT)

**Design Documentation (Centralizada):**
- `../party-media-gallery-docs/design/DESIGN_SYSTEM.md` - Sistema de diseño completo
- `../party-media-gallery-docs/BRAND_IDENTITY.md` - Identidad de marca

## Technology Stack

**Core:**
- Kotlin Multiplatform
- Compose Multiplatform 1.5.x
- Ktor Client 2.3.x (networking)
- SQLDelight 2.0.x (local database)
- Koin 3.5.x (dependency injection)
- Voyager 1.0.x (navigation)
- kotlinx.serialization 1.6.x
- Kotlinx DateTime, Okio 3.x

**Firebase Services:**
- Firebase Auth, Firestore, Storage, Cloud Messaging
- Firebase Analytics, Cloud Functions
- Collections: users/, party_events/, media_content/, chat_rooms/, messages/

**Platform-specific:**
- Android: CameraX 1.3.x, Material Design 3, WorkManager, ExoPlayer 2.19.x
- iOS: AVFoundation, Photos framework, UserNotifications
- Web: Browser File API, WebRTC, IndexedDB, Web Push API
- Desktop: JVM 17+, system tray integration

**Third-party APIs:**
- Social: Instagram, TikTok, Twitter, Facebook, Pinterest APIs
- Music: Spotify API for party playlists
- Maps: Google Maps API for venue integration
- Search: Algolia for party/venue search
- ML: Google Cloud Vision/Natural Language

## Development Commands

**Project Setup:**
```bash
# Build shared module
./gradlew :shared:build

# Run tests
./gradlew check

# Format code
./gradlew ktlintFormat
```

**Platform-specific builds:**
```bash
# Android
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug

# iOS Simulator (macOS only - Standard KMP Setup)
./gradlew iosSimulatorList              # List available simulators
./gradlew iosSimulatorBoot              # Boot default simulator (iPhone 15 Pro)
./gradlew iosBuildDebug                 # Build for simulator
./gradlew iosInstallSimulator           # Build + install on booted simulator
./gradlew iosRun                        # Full workflow: boot, build, install, launch

# iOS with custom simulator:
./gradlew iosRun -PiosSimulator="iPhone 16 Pro"

# Desktop
./gradlew :desktopApp:run

# Web
./gradlew :webApp:jsBrowserRun
```

**iOS Development Notes:**
- Requires Xcode installed with iOS Simulator runtimes
- Uses xcodebuild for compilation and xcrun simctl for simulator management
- **CURRENT**: iOS app is pure SwiftUI (in `iosApp/iosApp/`)
- **PLANNED**: Migration to Compose Multiplatform for iOS (see `docs/tickets/COMPOSE_MULTIPLATFORM_IOS_TICKETS.md`)
- Bundle ID: `com.partygallery.ios`
- Default simulator: iPhone 15 Pro (can be overridden with `-PiosSimulator`)

**⚠️ Xcode 26 Beta Note:**
If using Xcode 26 beta, the scheme may need regeneration:
1. Open Xcode: `open iosApp/iosApp.xcodeproj`
2. Product → Scheme → Manage Schemes → Delete "iosApp" → Autocreate Schemes
3. Set deployment target to match available simulators (iOS 26.0)
Alternatively, run directly from Xcode until scheme is fixed.

**Testing:**
```bash
# All tests
./gradlew check

# Platform-specific tests
./gradlew :shared:testDebugUnitTest        # Shared tests
./gradlew :androidApp:testDebugUnitTest    # Android tests
./gradlew :iosApp:iosSimulatorArm64Test    # iOS tests
```

## Code Style Requirements

- Follow official Kotlin coding conventions
- Use ktlint for formatting: `./gradlew ktlintFormat`
- 4 spaces indentation, no tabs
- Line length: 100 characters maximum
- PascalCase for classes and Compose functions
- camelCase for functions and variables
- SCREAMING_SNAKE_CASE for constants

## GitFlow - Branching Strategy (OBLIGATORIO)

Este proyecto utiliza **GitFlow** como estrategia de branching. Todo desarrollo DEBE seguir este flujo.

### Ramas Principales

| Rama | Propósito | Protegida |
|------|-----------|-----------|
| `main` | Código en producción, siempre estable | Sí |
| `develop` | Integración de features, base para desarrollo | Sí |

### Ramas de Soporte

| Tipo | Prefijo | Base | Merge a | Ejemplo |
|------|---------|------|---------|---------|
| Feature | `feature/` | develop | develop | `feature/S2-login-screen` |
| Bugfix | `bugfix/` | develop | develop | `bugfix/fix-avatar-crash` |
| Release | `release/` | develop | main + develop | `release/v1.0.0` |
| Hotfix | `hotfix/` | main | main + develop | `hotfix/critical-auth-fix` |

### Flujo de Desarrollo

```
1. Nueva Feature/Bugfix
   develop → feature/TICKET-ID-descripcion → PR → develop

2. Release
   develop → release/vX.Y.Z → QA → PR → main + develop → tag vX.Y.Z

3. Hotfix (producción)
   main → hotfix/descripcion → PR → main + develop → tag vX.Y.Z
```

### Convención de Nombres de Rama

```
feature/S{sprint}-{ticket}-{descripcion-corta}
bugfix/{ticket}-{descripcion-corta}
release/v{major}.{minor}.{patch}
hotfix/{descripcion-corta}
```

**Ejemplos:**
- `feature/S2-001-login-screen`
- `feature/S3-home-feed`
- `bugfix/S2-015-fix-validation`
- `release/v1.0.0`
- `hotfix/auth-token-refresh`

### Comandos Git Esenciales

```bash
# Crear feature branch
git checkout develop
git pull origin develop
git checkout -b feature/S2-001-login-screen

# Crear bugfix branch
git checkout develop
git pull origin develop
git checkout -b bugfix/fix-avatar-crash

# Crear release branch
git checkout develop
git pull origin develop
git checkout -b release/v1.0.0

# Crear hotfix branch
git checkout main
git pull origin main
git checkout -b hotfix/critical-fix

# Finalizar feature (merge a develop)
git checkout develop
git merge --no-ff feature/S2-001-login-screen
git push origin develop
git branch -d feature/S2-001-login-screen

# Finalizar release
git checkout main
git merge --no-ff release/v1.0.0
git tag -a v1.0.0 -m "Release v1.0.0"
git checkout develop
git merge --no-ff release/v1.0.0
git push origin main develop --tags
```

### Reglas GitFlow

1. **NUNCA** hacer commit directo a `main` o `develop`
2. **SIEMPRE** crear PR para merge
3. **SIEMPRE** usar `--no-ff` en merges para preservar historial
4. **SIEMPRE** incluir ticket ID en nombre de rama
5. **SIEMPRE** hacer rebase de develop antes de PR
6. **NUNCA** hacer force push a ramas protegidas

### PR Requirements

- Título: `[TICKET-ID] Descripción concisa`
- Tests pasando (CI verde)
- Coverage >= threshold actual
- Code review aprobado
- Sin conflictos con base branch

### Slash Command

Usa `/branch` para crear ramas con nomenclatura correcta automáticamente.

**Documentación completa:** `../party-media-gallery-docs/GITFLOW.md`

## CI/CD - Platform-Based Build System

El proyecto usa un sistema de CI/CD inteligente que optimiza los builds según la plataforma afectada.

### Workflows

| Workflow | Trigger | Propósito |
|----------|---------|-----------|
| `ci-full.yml` | Push a `main`, `release/*`, `fix/*` | Build completo de todas las plataformas |
| `pr-check.yml` | PRs y branches de desarrollo | Build selectivo según configuración |

### Branch-Based Auto-Detection

Los builds se activan automáticamente según el prefijo del branch:

| Prefijo | Plataforma | Ejemplo |
|---------|------------|---------|
| `android/*` | Solo Android | `android/fix-camera-crash` |
| `ios/*` | Solo iOS | `ios/update-notifications` |
| `desktop/*` | Solo Desktop | `desktop/tray-icon` |
| `web/*` | Solo Web | `web/dark-mode-fix` |
| `feature/*`, `bugfix/*` | Usa config file | `feature/S2-001-login` |

### CI Config File

Para PRs de `feature/*` o `bugfix/*`, el archivo `.github/ci-config.yml` controla qué builds corren:

```yaml
pr_checks:
  android: true    # Activar/desactivar build Android
  ios: true        # Activar/desactivar build iOS
  desktop: true    # Activar/desactivar build Desktop
  web: true        # Activar/desactivar build Web
  shared: true     # Siempre true (requerido)
```

### Instrucciones para Agentes

Cuando trabajes en un ticket que afecta una plataforma específica:

1. **Crea branch con prefijo de plataforma** si solo afecta una:
   ```bash
   git checkout -b android/S2-015-fix-camera
   ```

2. **Modifica `.github/ci-config.yml`** si afecta múltiples plataformas:
   ```yaml
   pr_checks:
     android: true   # Solo las plataformas afectadas
     ios: false
     desktop: true
     web: false
   ```

3. **Quality checks siempre corren** (ktlint, detekt)

4. **Shared module siempre se buildea** (es dependencia de todas las plataformas)

### Comandos Locales de CI

```bash
# Simular CI local
./gradlew check                           # Todos los checks
./gradlew ktlintCheck detekt              # Solo quality
./gradlew :shared:build                   # Solo shared
./gradlew :androidApp:assembleDebug       # Solo Android
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64  # Solo iOS
./gradlew :desktopApp:jar                 # Solo Desktop
./gradlew :webApp:jsBrowserProductionWebpack  # Solo Web
```

## TDD - Test-Driven Development (OBLIGATORIO)

Este proyecto sigue TDD estricto. Todo código nuevo DEBE:

### Flujo de Desarrollo
1. **Escribir test primero** - Define comportamiento esperado
2. **Ver test fallar (RED)** - Confirma que el test es válido
3. **Implementar mínimo (GREEN)** - Solo código necesario
4. **Refactorizar** - Mejora sin romper tests
5. **Repetir** - Siguiente feature/bug

### Reglas TDD
- NO se acepta código sin tests
- NO se acepta PR sin coverage > 50% (aumentará gradualmente a 95%)
- Tests deben fallar primero (verificable en CI)
- Un test, una responsabilidad

### Coverage Roadmap (Fases)
| Fase | Sprint | Coverage Mínimo |
|------|--------|-----------------|
| 1 | S2.6 | 50% |
| 2 | S3 | 60% |
| 3 | S4 | 70% |
| 4 | S5 | 80% |
| 5 | S6 | 90% |
| 6 | S7+ | **95%** (objetivo final) |

### Estructura de Tests
```
shared/src/
├── commonTest/          # Tests compartidos
│   ├── store/           # Store/ViewModel tests
│   ├── repository/      # Repository tests
│   └── fixtures/        # Test factories
├── androidTest/         # Android-specific tests
├── iosTest/             # iOS-specific tests
├── desktopTest/         # Desktop-specific tests
└── jsTest/              # Web-specific tests
```

### Comandos de Testing
```bash
# Ejecutar todos los tests
./gradlew check

# Tests por plataforma
./gradlew :shared:allTests
./gradlew :androidApp:testDebugUnitTest
./gradlew :shared:iosSimulatorArm64Test
./gradlew :shared:desktopTest
./gradlew :shared:jsTest
```

## Requirements & Setup

**Prerequisites:**
- JDK 17+
- Android Studio (latest stable)
- Xcode 15+ (for iOS development, macOS only)
- Node.js 18+ (for web development)

**Firebase Configuration Required:**
- android/google-services.json
- ios/GoogleService-Info.plist
- web/firebase-config.js

## Core Domain Models

**Key entities and their structures:**

- **User**: username, email, firstName, lastName, birthDate, avatarUrl, socialLinks, tags, followersCount
- **PartyEvent**: hostId, coHosts, title, venue, dateTime, coverImage, tags, musicGenres, status (PLANNED/LIVE/ENDED/CANCELLED)
- **PartyMediaContent**: type (PHOTO/VIDEO/AUDIO/DOCUMENT), partyEventId, partyMood (HYPE/CHILL/WILD/ROMANTIC/CRAZY/ELEGANT), social metrics
- **PartyChatRoom**: partyEventId, participants, isEventChat, lastMessage
- **PartyMessage**: content, type, mediaUrl, partyMoment flag, reactions

## App Flow & Navigation

**Signup Process (6 steps):**
1. Basic info (name, birthdate, username)
2. Profile avatar setup
3. Contact sync and search
4. Party interest tags selection
5. Social media linking (Instagram, TikTok, Twitter, Facebook, Pinterest)

**Main Navigation:**
- Home: Party content feed and live events
- Favorites: Curated content and event suggestions  
- Studio: Party content creation and event documentation
- Profile: User profile with party history

## Core Repositories & ViewModels

**Repositories:** ContactRepository, UserRepository, PartyTagRepository, PartyEventRepository, MediaRepository, AnalyticsRepository, ChatRepository, PartyRecommendationRepository

**State Stores per flow:** LoginStore, SignUpFlowStore (6 steps), HomeStore, FavoritesStore, StudioStore, ProfileStore, PartyEventStore, ChatStore

**Intent classes:** LoginIntent, SignUpIntent, HomeIntent, FavoritesIntent, StudioIntent, ProfileIntent, PartyEventIntent, ChatIntent

## Key Features

- Real-time party content sharing during events
- Party mood detection and filters
- Event-specific group chats
- Collaborative party albums
- Party discovery and recommendations
- Offline support for party content
- Music integration with party playlists

## Sistema de Agentes

Este proyecto utiliza un sistema de agentes Claude especializados para desarrollo autonomo.

**Documentacion Centralizada:**
- `../party-media-gallery-docs/agents/ORCHESTRATION.md` - Sistema de orquestacion
- `../party-media-gallery-docs/PROJECT_ROADMAP.md` - Roadmap maestro (389 tickets)
- `../party-media-gallery-docs/PROGRESS_TRACKING.md` - Estado real del codebase
- `../party-media-gallery-docs/README.md` - Hub de documentacion

**Symlinks locales (para compatibilidad):**
- `.claude/ORCHESTRATION.md` → docs/agents/ORCHESTRATION.md
- `.claude/SPRINT_PLAN.md` → docs/PROJECT_ROADMAP.md
- `.claude/agents/` → docs/agents/app/

**Auto-Routing de Tareas:**

| Tipo de Tarea | Agente | Estado |
|---------------|--------|--------|
| UI, screens, composables | @compose-developer | Activo |
| Repository implementations | @compose-developer | Pendiente (Gap crítico) |
| Multiplatform, expect/actual | @multiplatform-engineer | Activo |
| Tests de UI/Store | @compose-test-agent | Pendiente |
| Android Firebase/Camera | @android-developer | Pendiente |
| iOS Firebase/AVFoundation | @ios-developer | Pendiente |
| Web Firebase/Browser APIs | @web-developer | Activo |
| Bug, crash, error | @debugger | Activo |
| Review, PR | @code-reviewer | Activo |
| Build, CI, deploy | @devops | Activo |
| Principios SOLID | @SOLID-CLEAN-CODE | Activo |

**Sprint Actual: 2.5 (transición)**

| Sprint | Estado | Tickets |
|--------|--------|---------|
| S1: Foundation | 93% | 14/15 app (Firebase creds missing) |
| S1.5: DevOps | 100% | 8/8 |
| S2: Auth & Onboarding | 76% | 13/17 (tests pending) |
| S2.5: Data Layer | NEW | 7 tickets (Gap crítico) |
| S2.6: Testing Infra | NEW | 7 tickets |
| S2.7: Platform Integration | NEW | 6 tickets |
| S3+: Core Features | Adelantado | HomeStore, ProfileStore done early |

**Comandos Slash:**

| Comando | Descripcion |
|---------|-------------|
| `/ticket` | Crear ticket estructurado para agentes |
| `/branch` | Crear rama GitFlow con nomenclatura correcta |

**GitHub Project:**
- URL: https://github.com/orgs/Ismael-Playground/projects/1
- Todos los tickets se asignan automaticamente a este proyecto

## Important Notes

- This is proprietary software - all code contributions transfer ownership to the project maintainer
- Contributors must sign a CLA before any PR review
- No major features or architecture changes without prior approval
- All platforms should be tested when making changes
- Security best practices must be followed - no secrets in code or commits
- Content limits: Videos 2min max, Audio 3min max
- Target platforms: Android (SDK 24+), iOS (14.0+), Web (modern browsers), Desktop (JVM 17+)