rootProject.name = "PartyGallery"

include(":shared")
include(":androidApp")
include(":desktopApp")
include(":webApp")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}