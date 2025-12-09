plugins {
    // Kotlin Multiplatform
    kotlin("multiplatform") version Dependencies.kotlinVersion apply false
    kotlin("android") version Dependencies.kotlinVersion apply false

    // Android
    id("com.android.application") version Dependencies.androidGradlePluginVersion apply false
    id("com.android.library") version Dependencies.androidGradlePluginVersion apply false

    // Compose Multiplatform
    id("org.jetbrains.compose") version Dependencies.composeMultiplatformVersion apply false

    // Serialization
    kotlin("plugin.serialization") version Dependencies.kotlinVersion apply false

    // SQLDelight
    id("app.cash.sqldelight") version Dependencies.sqlDelightVersion apply false

    // Firebase
    id("com.google.gms.google-services") version Dependencies.googleServicesVersion apply false

    // Code Quality
    id("org.jlleitschuh.gradle.ktlint") version Dependencies.ktlintVersion apply false
    id("io.gitlab.arturbosch.detekt") version Dependencies.detektVersion apply false

    // Code Coverage
    id("org.jetbrains.kotlinx.kover") version Dependencies.koverVersion apply false
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

// Apply ktlint and detekt to all Kotlin projects
subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set(Dependencies.ktlintRulesVersion)
        android.set(true)
        outputToConsole.set(true)
        outputColorName.set("RED")
        ignoreFailures.set(false)
        enableExperimentalRules.set(false)
        filter {
            exclude("**/generated/**")
            exclude("**/build/**")
            exclude("**/*.gradle.kts")
        }
    }

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
        baseline = file("${rootProject.projectDir}/config/detekt/baseline.xml")
        parallel = true
        ignoreFailures = false
        autoCorrect = false
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = Dependencies.jvmTargetVersion
        reports {
            html.required.set(true)
            xml.required.set(true)
            txt.required.set(false)
            sarif.required.set(false)
        }
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

// ============================================================================
// iOS Simulator Tasks (Standard KMP Setup)
// ============================================================================

val iosSimulatorName = project.findProperty("iosSimulator") as? String ?: "iPhone 15 Pro"

tasks.register<Exec>("iosSimulatorBoot") {
    group = "ios"
    description = "Boot iOS Simulator"
    commandLine("xcrun", "simctl", "boot", iosSimulatorName)
    isIgnoreExitValue = true
}

tasks.register<Exec>("iosSimulatorList") {
    group = "ios"
    description = "List available iOS Simulators"
    commandLine("xcrun", "simctl", "list", "devices", "available")
}

tasks.register<Exec>("iosBuildDebug") {
    group = "ios"
    description = "Build iOS app for Simulator (Debug)"
    workingDir = file("iosApp")
    commandLine(
        "xcodebuild",
        "-project", "iosApp.xcodeproj",
        "-scheme", "iosApp",
        "-configuration", "Debug",
        "-sdk", "iphonesimulator",
        "-destination", "generic/platform=iOS Simulator",
        "-derivedDataPath", "${project.rootDir}/build/ios",
        "build"
    )
}

tasks.register<Exec>("iosInstallSimulator") {
    group = "ios"
    description = "Install iOS app on booted Simulator"
    dependsOn("iosBuildDebug")

    doFirst {
        // Find the built app
        val appPath = fileTree("${project.rootDir}/build/ios") {
            include("**/Debug-iphonesimulator/iosApp.app")
        }.files.firstOrNull()

        if (appPath != null) {
            commandLine("xcrun", "simctl", "install", "booted", appPath.absolutePath)
        } else {
            throw GradleException("iOS app not found. Build may have failed.")
        }
    }
}

tasks.register<Exec>("iosLaunchSimulator") {
    group = "ios"
    description = "Launch iOS app on Simulator"
    commandLine("xcrun", "simctl", "launch", "booted", "com.partygallery.iosApp")
}

tasks.register("iosRun") {
    group = "ios"
    description = "Build, install, and run iOS app on Simulator"
    dependsOn("iosSimulatorBoot", "iosInstallSimulator")

    doLast {
        exec {
            commandLine("xcrun", "simctl", "launch", "booted", "com.partygallery.iosApp")
        }
        println("\n✅ iOS app launched on Simulator!")
        println("📱 Simulator: $iosSimulatorName")
    }
}