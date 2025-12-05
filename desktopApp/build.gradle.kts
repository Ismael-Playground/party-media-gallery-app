plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDirs("src/jvmMain/kotlin")
            resources.srcDirs("src/jvmMain/resources")
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.foundation)

    // Koin
    implementation("io.insert-koin:koin-core:${Dependencies.koinVersion}")
}

compose.desktop {
    application {
        mainClass = "com.partygallery.desktop.MainKt"

        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb,
            )

            packageName = "Party Gallery"
            packageVersion = VersionConfig.getVersionNameForPackaging()
            description = "The ultimate multiplatform app for capturing and sharing party moments - v${VersionConfig.getVersionName()}"
            copyright = "© 2025 Party Gallery. All rights reserved."
            vendor = "Party Gallery"

            macOS {
                iconFile.set(project.file("src/jvmMain/resources/icon.icns"))
                bundleID = "com.partygallery.desktop"
                appCategory = "public.app-category.social-networking"
            }

            windows {
                iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
                menuGroup = "Party Gallery"
                upgradeUuid = "B9F6B8AA-9563-4203-A6AC-B7A85B96FAF3"
            }

            linux {
                iconFile.set(project.file("src/jvmMain/resources/icon.png"))
                packageName = "party-gallery"
                debMaintainer = "party-gallery@example.com"
                appCategory = "Network"
            }
        }
    }
}
