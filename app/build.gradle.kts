plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.victorkirui.meetnote"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.victorkirui.meetnote"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui.graphics)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    //Room Database
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.coroutines)

    //ViewModel
    implementation(libs.android.viewModel)

    //Koin
    implementation(libs.koin.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.core)

    //Coil
    implementation(libs.coil.compose)

    //Zxing
    implementation(libs.zxing.matrix)
    implementation(libs.zxing.display)

    //Test
    testImplementation(libs.coroutines.test)
    testImplementation(libs.mockk.test)

    //Datastore
    implementation(libs.datastore.preferences)

    //SplashScreen
    implementation(libs.splashscreen)

    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    //Compose Icons
    implementation(libs.jetpack.compose.icon)

    //Desugaring
    coreLibraryDesugaring(libs.desugaring)

    //Navigation
    implementation(libs.androidx.navigation.compose)
}