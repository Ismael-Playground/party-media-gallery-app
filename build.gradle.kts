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
    
    // Aplicar configuración de versión a todos los proyectos
    version = VersionConfig.getVersionName()
    
    // Logging de versión
    gradle.projectsEvaluated {
        println("🚀 Building Party Gallery v${VersionConfig.getVersionName()}")
        println("📦 Version Code: ${VersionConfig.getVersionCode()}")
        println("🏗️  Build Type: ${VersionConfig.getBuildType()}")
        println("🔧 Build Number: ${VersionConfig.getBuildNumber()}")
        println("📝 Git Hash: ${VersionConfig.getGitCommitHash()}")
    }
}

tasks.register("versionInfo") {
    group = "help"
    description = "Shows version information"
    
    doLast {
        val versionInfo = VersionConfig.getVersionInfo()
        
        println("\n" + "=".repeat(50))
        println("🎉 PARTY GALLERY VERSION INFO")
        println("=".repeat(50))
        
        versionInfo.forEach { (key, value) ->
            val displayKey = key.replaceFirstChar { it.uppercase() }
                .replace("([A-Z])".toRegex(), " $1")
                .trim()
            println("📋 $displayKey: $value")
        }
        
        println("=".repeat(50) + "\n")
    }
}

// tasks.register("clean", Delete::class) {
//     delete(rootProject.layout.buildDirectory)
// }