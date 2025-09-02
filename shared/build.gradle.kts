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
                jvmTarget = Dependencies.jvmTargetVersion
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
                implementation("io.ktor:ktor-client-core:${Dependencies.ktorVersion}")
                implementation("io.ktor:ktor-client-content-negotiation:${Dependencies.ktorVersion}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${Dependencies.ktorVersion}")
                
                // Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Dependencies.kotlinxSerializationVersion}")
                
                // DateTime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${Dependencies.kotlinxDatetimeVersion}")
                
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.kotlinxCoroutinesVersion}")
                
                // DI
                implementation("io.insert-koin:koin-core:${Dependencies.koinVersion}")
                implementation("io.insert-koin:koin-compose:${Dependencies.koinComposeVersion}")
                
                // Navigation
                implementation("cafe.adriel.voyager:voyager-navigator:${Dependencies.voyagerVersion}")
                implementation("cafe.adriel.voyager:voyager-koin:${Dependencies.voyagerVersion}")
                
                // File I/O
                implementation("com.squareup.okio:okio:${Dependencies.okioVersion}")
                
                // UUID
                implementation("com.benasher44:uuid:${Dependencies.uuidVersion}")
            }
        }
        
        val androidMain by getting {
            dependencies {
                // Android Compose
                implementation("androidx.compose.ui:ui-tooling-preview:${Dependencies.composeCompilerVersion}")
                implementation("androidx.activity:activity-compose:${Dependencies.activityComposeVersion}")
                
                // Ktor Android
                implementation("io.ktor:ktor-client-okhttp:${Dependencies.ktorVersion}")
                
                // Firebase Android
                implementation("com.google.firebase:firebase-auth-ktx:${Dependencies.firebaseAuthVersion}")
                implementation("com.google.firebase:firebase-firestore-ktx:${Dependencies.firebaseFirestoreVersion}")
                implementation("com.google.firebase:firebase-storage-ktx:${Dependencies.firebaseStorageVersion}")
                implementation("com.google.firebase:firebase-messaging-ktx:${Dependencies.firebaseMessagingVersion}")
                
                // Android specific
                implementation("androidx.work:work-runtime-ktx:${Dependencies.workManagerVersion}")
                implementation("androidx.camera:camera-camera2:${Dependencies.cameraXVersion}")
                implementation("androidx.camera:camera-lifecycle:${Dependencies.cameraXVersion}")
                implementation("androidx.camera:camera-view:${Dependencies.cameraXVersion}")
            }
        }
        
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                // Ktor iOS
                implementation("io.ktor:ktor-client-darwin:${Dependencies.ktorVersion}")
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
                implementation("io.ktor:ktor-client-cio:${Dependencies.ktorVersion}")
            }
        }
        
        val jsMain by getting {
            dependencies {
                // Ktor JS
                implementation("io.ktor:ktor-client-js:${Dependencies.ktorVersion}")
                
                // Compose Web
                implementation(compose.html.core)
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.insert-koin:koin-test:${Dependencies.koinTestVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Dependencies.kotlinxCoroutinesVersion}")
            }
        }
    }
}

android {
    namespace = "com.partygallery.shared"
    compileSdk = Dependencies.compileSdkVersion
    
    defaultConfig {
        minSdk = Dependencies.minSdkVersion
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