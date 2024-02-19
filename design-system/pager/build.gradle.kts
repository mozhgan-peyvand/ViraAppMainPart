plugins {
    id(libs.plugins.androidLibrary.get().pluginId)
    id(libs.plugins.kotlinAndroid.get().pluginId)
}

android {
    namespace = "ai.ivira.app.designsystem.pager"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.designSystem.theme)

    implementation(libs.coreKtx)

    // compose
    implementation(libs.composeUi)
    implementation(libs.composeUiGraphics)
    implementation(libs.composeUiToolingPreview)
    implementation(libs.composeCompiler)
    implementation(libs.composeFoundation)
    implementation(libs.composeMaterial)
    implementation(libs.composeRuntime)
    debugImplementation(libs.composeUiTooling)
    debugImplementation(libs.composeUiTestManifest)


    // coroutine
    implementation(libs.coroutineCore)
    implementation(libs.coroutineAndroid)

    implementation(libs.snapper)
}
