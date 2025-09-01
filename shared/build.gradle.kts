@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
    // id("app.cash.sqldelight")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    jvm("desktop")
    
    js(IR) {
        browser()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                
                // Networking
                implementation("io.ktor:ktor-client-core:2.3.6")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.6")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")
                
                // Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.1")
                
                // DateTime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                
                // DI
                implementation("io.insert-koin:koin-core:3.5.0")
                implementation("io.insert-koin:koin-compose:1.1.0")
                
                // Navigation
                implementation("cafe.adriel.voyager:voyager-navigator:1.0.0")
                implementation("cafe.adriel.voyager:voyager-koin:1.0.0")
                
                // File I/O
                implementation("com.squareup.okio:okio:3.6.0")
                
                // UUID
                implementation("com.benasher44:uuid:0.8.2")
            }
        }
        
        val androidMain by getting {
            dependencies {
                // Android Compose
                implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
                implementation("androidx.activity:activity-compose:1.8.1")
                
                // Ktor Android
                implementation("io.ktor:ktor-client-okhttp:2.3.6")
                
                // Firebase Android
                implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
                implementation("com.google.firebase:firebase-firestore-ktx:24.9.1")
                implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
                implementation("com.google.firebase:firebase-messaging-ktx:23.4.0")
                
                // Android specific
                implementation("androidx.work:work-runtime-ktx:2.8.1")
                implementation("androidx.camera:camera-camera2:1.3.0")
                implementation("androidx.camera:camera-lifecycle:1.3.0")
                implementation("androidx.camera:camera-view:1.3.0")
            }
        }
        
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                // Ktor iOS
                implementation("io.ktor:ktor-client-darwin:2.3.6")
            }
        }
        
        val iosX64Main by getting {
            dependsOn(iosMain)
        }
        
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        
        val desktopMain by getting {
            dependencies {
                // Compose Desktop
                implementation(compose.desktop.currentOs)
                
                // Ktor JVM
                implementation("io.ktor:ktor-client-cio:2.3.6")
            }
        }
        
        val jsMain by getting {
            dependencies {
                // Ktor JS
                implementation("io.ktor:ktor-client-js:2.3.6")
                
                // Compose Web
                implementation(compose.html.core)
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.insert-koin:koin-test:3.5.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
            }
        }
    }
}

android {
    namespace = "com.partygallery.shared"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// sqldelight {
//     databases {
//         create("PartyGalleryDatabase") {
//             packageName.set("com.partygallery.database")
//         }
//     }
// }