plugins {
    kotlin("js")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport {
                        enabled.set(true)
                    }
                }
            }
        }
        binaries.executable()
    }
}

dependencies {
    implementation(project(":shared"))
    
    // Compose Web
    implementation(compose.web.core)
    implementation(compose.runtime)
    
    // Koin
    implementation("io.insert-koin:koin-core:3.5.0")
    
    // Testing
    testImplementation(kotlin("test-js"))
}

compose.web {
    
}