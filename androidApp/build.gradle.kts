plugins {
    kotlin("android")
    id("com.android.application")
    id("org.jetbrains.compose")
    // id("com.google.gms.google-services") // Disabled until Firebase is configured
}

android {
    namespace = "com.partygallery.android"
    compileSdk = Dependencies.compileSdkVersion
    
    defaultConfig {
        applicationId = "com.partygallery.android"
        minSdk = Dependencies.minSdkVersion
        targetSdk = Dependencies.targetSdkVersion
        versionCode = VersionConfig.getVersionCode()
        versionName = VersionConfig.getVersionName()
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Agregar información de build como BuildConfig
        buildConfigField("String", "BUILD_NUMBER", "\"${VersionConfig.getBuildNumber()}\"")
        buildConfigField("String", "GIT_HASH", "\"${VersionConfig.getGitCommitHash()}\"")
        buildConfigField("String", "BUILD_TIME", "\"${System.currentTimeMillis()}\"")
        buildConfigField("boolean", "IS_CI_BUILD", "${VersionConfig.isReleaseBuild()}")
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = Dependencies.jvmTargetVersion
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = Dependencies.composeCompilerVersion
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":shared"))
    
    // Android Compose BOM
    implementation(platform("androidx.compose:compose-bom:${Dependencies.composeBomVersion}"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    
    // Activity Compose
    implementation("androidx.activity:activity-compose:${Dependencies.activityComposeVersion}")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${Dependencies.lifecycleViewModelComposeVersion}")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:${Dependencies.lifecycleRuntimeComposeVersion}")
    
    // Koin Android
    implementation("io.insert-koin:koin-android:${Dependencies.koinAndroidVersion}")
    implementation("io.insert-koin:koin-androidx-compose:${Dependencies.koinAndroidxComposeVersion}")
    
    // Testing
    testImplementation("junit:junit:${Dependencies.junitVersion}")
    androidTestImplementation("androidx.test.ext:junit:${Dependencies.androidxJunitVersion}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Dependencies.espressoVersion}")
    androidTestImplementation(platform("androidx.compose:compose-bom:${Dependencies.composeBomVersion}"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}