plugins {
    // Kotlin Multiplatform
    kotlin("multiplatform") version "1.9.20" apply false
    kotlin("android") version "1.9.20" apply false
    
    // Android
    id("com.android.application") version "8.1.4" apply false
    id("com.android.library") version "8.1.4" apply false
    
    // Compose Multiplatform
    id("org.jetbrains.compose") version "1.5.11" apply false
    
    // Serialization
    kotlin("plugin.serialization") version "1.9.20" apply false
    
    // SQLDelight
    id("app.cash.sqldelight") version "2.0.0" apply false
    
    // Firebase
    id("com.google.gms.google-services") version "4.4.0" apply false
}

allprojects {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        mavenCentral()
    }
}

// tasks.register("clean", Delete::class) {
//     delete(rootProject.layout.buildDirectory)
// }