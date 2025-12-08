# Compose Multiplatform para iOS - Tickets de Implementacion

> **Objetivo**: Migrar la app iOS de SwiftUI puro a Compose Multiplatform para compartir codigo UI con Android, Desktop y Web.
>
> **Estado Actual**:
> - iOS usa SwiftUI nativo en `iosApp/iosApp/` (ContentView.swift)
> - El modulo `shared/` ya tiene targets iOS configurados (iosX64, iosArm64, iosSimulatorArm64)
> - Compose Multiplatform plugin ya esta en build.gradle.kts
> - Desktop y Web ya usan MainApp() composable compartido

---

## Ticket 1: Configurar iOS Framework para Compose Multiplatform

### Descripcion

Configurar el modulo shared para generar un framework iOS que exponga la UI de Compose Multiplatform. Esto requiere agregar la configuracion de cocoapods o XCFramework en Gradle y configurar las dependencias necesarias para Compose iOS.

### Contexto para Agente

#### Documentacion Requerida
| Documento | Proposito | Ruta |
|-----------|-----------|------|
| CLAUDE.md | Stack y arquitectura | `CLAUDE.md` |
| Compose Multiplatform iOS | Documentacion oficial | https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-getting-started.html |
| KMP iOS Setup | Configuracion KMP | https://kotlinlang.org/docs/multiplatform-mobile-ios-dependencies.html |

#### Configuracion Actual (shared/build.gradle.kts)
```kotlin
// Targets iOS ya configurados
iosX64()
iosArm64()
iosSimulatorArm64()

// iosMain ya existe con Ktor Darwin
val iosMain by creating {
    dependsOn(commonMain)
    dependencies {
        implementation("io.ktor:ktor-client-darwin:${Dependencies.ktorVersion}")
    }
}
```

#### Codigo de Referencia
| Archivo | Para que revisar |
|---------|------------------|
| `shared/build.gradle.kts` | Configuracion actual de targets |
| `build.gradle.kts` | Plugins y versiones |
| `buildSrc/src/main/kotlin/Dependencies.kt` | Versiones de dependencias |
| `desktopApp/build.gradle.kts` | Como se configura Desktop |

#### Framework Configuration Requerida
```kotlin
// Agregar en shared/build.gradle.kts
kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }
}
```

### Subagentes Asignados
| Agente | Rol | Archivo |
|--------|-----|---------|
| `@multiplatform-engineer` | Configurar framework iOS | `.claude/agents/frontend/multiplatform-engineer.md` |
| `@devops` | Verificar build iOS | `.claude/agents/operations/devops.md` |

### Archivos a Crear
- (ninguno)

### Archivos a Modificar
- `shared/build.gradle.kts` - Agregar configuracion de framework iOS
- `buildSrc/src/main/kotlin/Dependencies.kt` - Agregar versiones si es necesario

### Criterios de Aceptacion
- [ ] Framework iOS se genera correctamente con `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64`
- [ ] Framework incluye Compose runtime y UI
- [ ] Build no tiene errores ni warnings criticos
- [ ] Framework es compatible con iOS 14.0+

### Instrucciones de Implementacion
1. Leer `shared/build.gradle.kts` actual
2. Agregar configuracion de `binaries.framework` para cada target iOS
3. Configurar `isStatic = true` para mejor compatibilidad
4. Agregar `export()` de dependencias necesarias si aplica
5. Ejecutar `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64`
6. Verificar que el framework se genera en `shared/build/bin/`
7. Verificar que no hay errores de compilacion

### Prioridad
P1 - Prerequisito para los demas tickets

---

## Ticket 2: Crear MainViewController para Compose UI en iOS

### Descripcion

Crear un UIViewController en Swift que hostee la UI de Compose Multiplatform usando ComposeUIViewController. Este controller servira como puente entre SwiftUI y Compose.

### Contexto para Agente

#### Documentacion Requerida
| Documento | Proposito | Ruta |
|-----------|-----------|------|
| CLAUDE.md | Stack y arquitectura | `CLAUDE.md` |
| Compose iOS Integration | ComposeUIViewController | https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-compose-in-swiftui.html |

#### Arquitectura de Integracion
```
SwiftUI (iOSApp.swift)
    └── ContentView.swift
        └── UIViewControllerRepresentable
            └── ComposeUIViewController (Kotlin)
                └── MainApp() (Compose commonMain)
```

#### Codigo Kotlin Requerido (iosMain)
```kotlin
// shared/src/iosMain/kotlin/MainViewController.kt
import androidx.compose.ui.window.ComposeUIViewController
import com.partygallery.presentation.ui.MainApp

fun MainViewController() = ComposeUIViewController { MainApp() }
```

#### Codigo Swift Requerido
```swift
// iosApp/iosApp/ComposeView.swift
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

#### Codigo de Referencia
| Archivo | Para que revisar |
|---------|------------------|
| `shared/src/commonMain/.../MainApp.kt` | Composable raiz |
| `desktopApp/src/jvmMain/.../Main.kt` | Como Desktop usa MainApp |
| `iosApp/iosApp/ContentView.swift` | SwiftUI actual |

#### MainApp Composable Existente
```kotlin
// shared/src/commonMain/.../MainApp.kt
@Composable
fun MainApp() {
    PartyGalleryTheme {
        Navigator(
            screen = LoginScreen(),
            onBackPressed = { /* ... */ }
        ) { navigator ->
            // Voyager navigation
        }
    }
}
```

### Subagentes Asignados
| Agente | Rol | Archivo |
|--------|-----|---------|
| `@multiplatform-engineer` | Crear MainViewController.kt | `.claude/agents/frontend/multiplatform-engineer.md` |
| `@compose-developer` | Verificar MainApp() | `.claude/agents/frontend/compose-developer.md` |

### Archivos a Crear
- `shared/src/iosMain/kotlin/com/partygallery/MainViewController.kt`
- `iosApp/iosApp/ComposeView.swift`

### Archivos a Modificar
- (ninguno en este ticket)

### Criterios de Aceptacion
- [ ] MainViewController.kt compila sin errores
- [ ] ComposeView.swift importa el framework shared correctamente
- [ ] UIViewControllerRepresentable funciona con ComposeUIViewController
- [ ] No hay memory leaks ni crashes

### Instrucciones de Implementacion
1. Crear `shared/src/iosMain/kotlin/com/partygallery/MainViewController.kt`
2. Importar ComposeUIViewController de Compose Multiplatform
3. Crear funcion `MainViewController()` que retorne ComposeUIViewController con MainApp()
4. Crear `iosApp/iosApp/ComposeView.swift` con UIViewControllerRepresentable
5. Verificar que el import `import shared` funciona
6. Probar que ComposeView se puede instanciar

### Prioridad
P1 - Core feature para iOS Compose

### Dependencias
- Ticket 1 completado (Framework iOS configurado)

---

## Ticket 3: Migrar iOS App a Compose Multiplatform

### Descripcion

Modificar la app iOS para usar ComposeView en lugar del SwiftUI ContentView actual. Mantener el wrapper SwiftUI minimo y delegar toda la UI a Compose.

### Contexto para Agente

#### Documentacion Requerida
| Documento | Proposito | Ruta |
|-----------|-----------|------|
| CLAUDE.md | Stack y arquitectura | `CLAUDE.md` |
| Brand Identity | Colores y design | `../party-media-gallery-docs/BRAND_IDENTITY.md` |

#### Design System (Dark Mode First)
| Token | Valor | Uso |
|-------|-------|-----|
| background | #0A0A0A | Fondo principal |
| surface | #141414 | Cards base |
| primary | #F59E0B | Acentos (Amber) |
| onSurface | #FFFFFF | Texto principal |
| onSurfaceVariant | #A1A1AA | Texto secundario |

#### Estado Actual iOS (SwiftUI)
```swift
// iosApp/iosApp/ContentView.swift - A REEMPLAZAR
struct ContentView: View {
    // SwiftUI login form nativo
    // ~270 lineas de codigo SwiftUI
}
```

#### Estado Objetivo iOS (Compose)
```swift
// iosApp/iosApp/ContentView.swift - NUEVO
import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
    }
}
```

#### Codigo de Referencia
| Archivo | Para que revisar |
|---------|------------------|
| `iosApp/iosApp/ContentView.swift` | SwiftUI actual a reemplazar |
| `iosApp/iosApp/iOSApp.swift` | Entry point |
| `shared/src/commonMain/.../LoginScreen.kt` | Login en Compose |
| `shared/src/commonMain/.../MainScreen.kt` | Main navigation en Compose |

### Subagentes Asignados
| Agente | Rol | Archivo |
|--------|-----|---------|
| `@multiplatform-engineer` | Integracion iOS-Compose | `.claude/agents/frontend/multiplatform-engineer.md` |
| `@compose-developer` | Verificar UI | `.claude/agents/frontend/compose-developer.md` |

### Archivos a Crear
- (ninguno - usar ComposeView.swift del Ticket 2)

### Archivos a Modificar
- `iosApp/iosApp/ContentView.swift` - Reemplazar con ComposeView wrapper
- `iosApp/iosApp/iOSApp.swift` - Ajustar si es necesario

### Archivos a Eliminar/Archivar
- `iosApp/iosApp/ContentView.swift` (el original) - Archivar como `ContentView.swift.swiftui.backup`

### Criterios de Aceptacion
- [ ] iOS app muestra LoginScreen de Compose (no SwiftUI)
- [ ] Dark Mode First: background #0A0A0A, primary #F59E0B
- [ ] Login flow funciona completamente
- [ ] SignUp flow funciona (6 pasos)
- [ ] MainScreen con BottomNavBar funciona
- [ ] Navigation entre screens funciona
- [ ] No hay crashes ni memory leaks
- [ ] App funciona en iPhone y iPad

### Instrucciones de Implementacion
1. Hacer backup de `ContentView.swift` actual
2. Reemplazar contenido con wrapper simple para ComposeView
3. Ajustar `iOSApp.swift` si es necesario (preferredColorScheme, etc)
4. Rebuild del framework: `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64`
5. Copiar framework a Xcode project si es necesario
6. Build iOS app en Xcode
7. Probar en simulator:
   - Login con cualquier email
   - Verificar transicion a MainScreen
   - Navegar entre tabs
   - Verificar colores Dark Mode First

### Prioridad
P1 - Core feature para iOS Compose

### Dependencias
- Ticket 1 completado (Framework iOS)
- Ticket 2 completado (MainViewController + ComposeView)

---

## Ticket 4: Implementar Platform Implementations (expect/actual) para iOS

### Descripcion

Implementar las funciones `actual` para iOS de las declaraciones `expect` en commonMain. Esto incluye platform-specific features como permisos, camara, galeria, y otros.

### Contexto para Agente

#### Documentacion Requerida
| Documento | Proposito | Ruta |
|-----------|-----------|------|
| CLAUDE.md | Stack y arquitectura | `CLAUDE.md` |
| KMP expect/actual | Patron multiplatform | https://kotlinlang.org/docs/multiplatform-connect-to-apis.html |

#### Patron expect/actual
```kotlin
// commonMain - Declaracion
expect fun getPlatformName(): String

// iosMain - Implementacion
actual fun getPlatformName(): String = "iOS"
```

#### Expects Existentes (a revisar en commonMain)
```kotlin
// Buscar en shared/src/commonMain/kotlin/
expect class Platform {
    val name: String
}

expect fun getDispatchers(): AppDispatchers

// Posibles expects adicionales:
expect fun openCamera()
expect fun openGallery()
expect fun requestPermission(permission: Permission): Boolean
expect fun shareContent(content: String)
```

#### Codigo de Referencia
| Archivo | Para que revisar |
|---------|------------------|
| `shared/src/commonMain/.../Platform.kt` | Expects existentes |
| `shared/src/androidMain/.../Platform.kt` | Actuals Android |
| `shared/src/desktopMain/.../Platform.kt` | Actuals Desktop |
| `shared/src/iosMain/` | Directorio para actuals iOS |

#### iOS Platform APIs
```kotlin
// Ejemplo de implementaciones iOS necesarias
import platform.UIKit.*
import platform.Foundation.*
import platform.Photos.*

actual class Platform actual constructor() {
    actual val name: String = UIDevice.currentDevice.systemName() + " " +
                              UIDevice.currentDevice.systemVersion
}

actual fun getDispatchers(): AppDispatchers = IosDispatchers()
```

### Subagentes Asignados
| Agente | Rol | Archivo |
|--------|-----|---------|
| `@multiplatform-engineer` | Implementar actuals iOS | `.claude/agents/frontend/multiplatform-engineer.md` |
| `@kotest-agent` | Tests para actuals | `.claude/agents/frontend/kotest-agent.md` |

### Archivos a Crear
- `shared/src/iosMain/kotlin/com/partygallery/Platform.ios.kt`
- `shared/src/iosMain/kotlin/com/partygallery/di/PlatformModule.ios.kt` (si aplica)
- Otros archivos `.ios.kt` segun expects encontrados

### Archivos a Modificar
- (depende de los expects existentes)

### Criterios de Aceptacion
- [ ] Todos los `expect` tienen su `actual` en iosMain
- [ ] No hay errores de "Expected declaration has no actual"
- [ ] Platform implementations usan APIs iOS nativas correctamente
- [ ] Build iOS compila sin errores
- [ ] Tests unitarios para actuals pasan

### Instrucciones de Implementacion
1. Ejecutar `./gradlew :shared:compileKotlinIosSimulatorArm64` para ver expects faltantes
2. Buscar todas las declaraciones `expect` en commonMain:
   ```bash
   grep -r "expect " shared/src/commonMain/kotlin/
   ```
3. Para cada expect, crear archivo `.ios.kt` correspondiente en iosMain
4. Implementar usando APIs iOS nativas (platform.UIKit, platform.Foundation, etc.)
5. Para Dispatchers, usar:
   ```kotlin
   actual fun getDispatchers() = object : AppDispatchers {
       override val main = Dispatchers.Main
       override val io = Dispatchers.Default
       override val default = Dispatchers.Default
   }
   ```
6. Verificar build completo: `./gradlew :shared:linkDebugFrameworkIosSimulatorArm64`
7. Correr tests si existen

### Prioridad
P2 - Necesario para funcionalidad completa

### Dependencias
- Ticket 1 completado (Framework iOS)

---

## Resumen de Orden de Ejecucion

```
Ticket 1: Framework iOS Configuration
    ↓
Ticket 4: expect/actual implementations  (puede ser paralelo con 2)
    ↓
Ticket 2: MainViewController + ComposeView
    ↓
Ticket 3: Migrar iOS App a Compose
```

## Comandos de Verificacion

```bash
# Verificar build del framework iOS
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Ver expects sin actuals
./gradlew :shared:compileKotlinIosSimulatorArm64 2>&1 | grep "Expected"

# Build iOS app (desde iosApp/)
xcodebuild -project iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator build

# Run en simulator
xcrun simctl boot "iPhone 15 Pro"
xcrun simctl install booted build/Debug-iphonesimulator/PartyGallery.app
xcrun simctl launch booted com.partygallery.ios
```

## Notas Adicionales

### Xcode Project Setup
El proyecto Xcode necesitara:
1. Agregar el framework generado a "Frameworks, Libraries, and Embedded Content"
2. Configurar "Framework Search Paths" a `$(SRCROOT)/../shared/build/bin/iosSimulatorArm64/debugFramework`
3. O usar CocoaPods/SPM si se configura

### iOS Minimum Version
- Deployment Target actual: iOS 14.0
- Compose Multiplatform requiere iOS 14.0+
- Compatible con configuracion actual

### Dark Mode
- Compose maneja su propio theme (PartyGalleryTheme)
- SwiftUI wrapper debe usar `.preferredColorScheme(.dark)` o delegarlo a Compose
- Colores ya estan definidos en `shared/src/commonMain/.../theme/`

---

*Tickets generados para Party Gallery iOS Compose Multiplatform Migration*
*Fecha: Diciembre 2024*
*GitHub Project: https://github.com/orgs/Ismael-Playground/projects/1*
