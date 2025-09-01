rootProject.name = "PartyGallery"

include(":shared")
include(":androidApp")
include(":desktopApp")
include(":webApp")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // Repositorio para Compose Multiplatform
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // También aquí por si acaso
        google()
        mavenCentral()
    }
}