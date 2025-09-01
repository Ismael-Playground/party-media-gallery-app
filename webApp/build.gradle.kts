plugins {
    kotlin("multiplatform")
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
    
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":shared"))
                
                // Compose Web
                implementation(compose.web.core)
                implementation(compose.runtime)
                
                // Koin
                implementation("io.insert-koin:koin-core:3.5.0")
            }
        }
        
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

// Generar archivo de versión para la web
tasks.register("generateVersionFile") {
    doLast {
        val buildNumber = System.getenv("GITHUB_RUN_NUMBER") 
            ?: System.getenv("CI_PIPELINE_IID") 
            ?: System.getenv("BUILD_NUMBER") 
            ?: "local"
            
        val versionName = "$version"
        val currentTime = System.currentTimeMillis().toString()
        
        val versionJs = """
            window.APP_VERSION = {
              "version": "$versionName",
              "buildNumber": "$buildNumber",
              "buildTime": "$currentTime",
              "isCiBuild": ${System.getenv("CI") == "true"}
            };
        """.trimIndent()
        
        file("src/jsMain/resources/version.js").writeText(versionJs)
    }
}

// Ejecutar antes de compilar
tasks.named("compileKotlinJs") {
    dependsOn("generateVersionFile")
}