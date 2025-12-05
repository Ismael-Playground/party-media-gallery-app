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