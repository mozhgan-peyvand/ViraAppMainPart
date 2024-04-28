import com.android.build.gradle.internal.tasks.FinalizeBundleTask
import java.io.ByteArrayOutputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hilt)
    id(libs.plugins.kotlinKapt.get().pluginId)
    id(libs.plugins.kotlinParcelize.get().pluginId)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.sentry)
}

val versionEpoch = 3 // must NOT CHANGE, added for historical reasons
val versionMajor = 1
val versionMinor = 0
val versionPatch = 0
val versionOffset = 0

val versionMinSdk = 21
val versionCompileSdk = 34
val versionTargetSdk = 33

val applicationIdStr = "ai.ivira.app"

fun generateVersionCode(): Int {
    val offsetPart = "$versionOffset"
    val patchPart = String.format("%2d", versionPatch).replace(" ", "0")
    val minorPart = String.format("%2d", versionMinor).replace(" ", "0")
    val majorPart = String.format("%2d", versionMajor).replace(" ", "0")
    val epochPart = "$versionEpoch"

    return Integer.parseInt("$epochPart$majorPart$minorPart$patchPart$offsetPart")
}

fun generateVersionName(): String {
    return "$versionMajor.$versionMinor.$versionPatch"
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

sentry {
    includeProguardMapping.set(false)
    autoUploadProguardMapping.set(false)
}

android {
    namespace = applicationIdStr
    compileSdk = versionCompileSdk

    defaultConfig {
        applicationId = applicationIdStr
        minSdk = versionMinSdk
        targetSdk = versionTargetSdk
        versionCode = generateVersionCode()
        versionName = generateVersionName()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        getByName("debug") {
            val props = file(".signing-debug/signing.properties")
            val keystore = file(".signing-debug/debug.jks")
            if (props.exists()) {
                val signing = Properties()
                props.inputStream().use { signing.load(it) }
                if (signing.containsKey("KEYSTORE_PASSWORD")) {
                    storePassword = signing.getProperty("KEYSTORE_PASSWORD")
                }
                if (signing.containsKey("KEY_PASSWORD")) {
                    keyPassword = signing.getProperty("KEY_PASSWORD")
                }
                if (signing.containsKey("KEY_ALIAS")) {
                    keyAlias = signing.getProperty("KEY_ALIAS")
                }
            }
            if (keystore.exists()) {
                storeFile = keystore
            }
        }
        create("release") {
            val props = file(".signing/signing.properties")
            val keystore = file(".signing/release.jks")
            if (props.exists()) {
                val signing = Properties()
                props.inputStream().use { signing.load(it) }
                if (signing.containsKey("KEYSTORE_PASSWORD")) {
                    storePassword = signing.getProperty("KEYSTORE_PASSWORD")
                }
                if (signing.containsKey("KEY_PASSWORD")) {
                    keyPassword = signing.getProperty("KEY_PASSWORD")
                }
                if (signing.containsKey("KEY_ALIAS")) {
                    keyAlias = signing.getProperty("KEY_ALIAS")
                }
            }
            if (keystore.exists()) {
                storeFile = keystore
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
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
        freeCompilerArgs += "-Xcontext-receivers"
        freeCompilerArgs += "-opt-in=androidx.compose.material.ExperimentalMaterialApi"
        freeCompilerArgs += "-opt-in=com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi"
        freeCompilerArgs += "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
        freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        freeCompilerArgs += "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi"
        freeCompilerArgs += "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        freeCompilerArgs += "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi"
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    // source is used to configure sentry, if changed that also must be changed! (specially source)
    flavorDimensionList += listOf("source", "store")
    productFlavors {
        create("dev") {
            applicationIdSuffix = ".dev"
            dimension = "source"
        }
        create("prod") {
            dimension = "source"
        }

        create("cafeBazaar") {
            dimension = "store"
            buildConfigField("String", "SHARE_URL", "\"https://cafebazaar.ir/app/ai.ivira.app\"")
        }

        create("myket") {
            dimension = "store"
            buildConfigField("String", "SHARE_URL", "\"https://myket.ir/app/ai.ivira.app\"")
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    @Suppress("DEPRECATION")
    applicationVariants.all {
        val variant = this

        val artifactName = "Vira-v${generateVersionName()}(${variant.versionCode})-${variant.flavorName}${variant.buildType.name.capitalize()}-g${getGitHash()}"

        // rename apks
        outputs.map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { variantOutput ->
                variantOutput.outputFileName = "${artifactName}.apk"
            }

        // rename bundles
        tasks.named("sign${variant.name.capitalize()}Bundle", FinalizeBundleTask::class) {
            val file = finalBundleFile.asFile.get()
            val finalFile = File(file.parentFile, "$artifactName.aab")
            finalBundleFile.set(finalFile)
        }
    }

    ndkVersion = "24.0.8215888"
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

androidComponents {
    fun List<Pair<String, String>>.contains(name: String): Boolean {
        return any { name in listOf(it.first, it.second) }
    }
    beforeVariants { variantBuilder ->
        with(variantBuilder.productFlavors) {
            if (contains("myket") &&
                (contains("dev") || variantBuilder.buildType == "debug")
            ) {
                variantBuilder.enable = false
            }
        }
    }
}

dependencies {

    implementation(projects.designSystem.theme)
    implementation(projects.designSystem.bottomsheet)
    implementation(projects.designSystem.pager)

    implementation(libs.coreKtx)
    implementation(libs.coreSplashScreen)

    // lifecycle
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.lifecycleRuntimeCompose)
    implementation(libs.lifecycleViewModelCompose)
    implementation(libs.lifecycleProcess)

    // compose
    implementation(libs.acticityCompose)
    implementation(libs.composeUi)
    implementation(libs.composeUiGraphics)
    implementation(libs.composeUiToolingPreview)
    implementation(libs.composeCompiler)
    implementation(libs.composeFoundation)
    implementation(libs.composeMaterial)
    implementation(libs.composeRuntime)
    debugImplementation(libs.composeUiTooling)
    debugImplementation(libs.composeUiTestManifest)

    // accompanist
    implementation(libs.accompanistNavigationMaterial)

    // dataStore Preferences
    implementation(libs.dataStorePreferences)

    // hilt
    implementation(libs.hiltAndroid)
    kapt(libs.hiltCompiler)
    implementation(libs.hiltNavigationCompose)

    // coroutine
    implementation(libs.coroutineCore)
    implementation(libs.coroutineAndroid)

    // Room
    implementation(libs.roomRuntime)
    ksp(libs.roomCompiler)
    implementation(libs.roomKtx)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofitMoshiConverter)
    implementation(libs.retrofitScalarConverter)
    implementation(libs.okHttp)
    implementation(libs.okHttpLoggingInterceptor)

    // Moshi Kotlin
    ksp(libs.moshiCodeGen)
    implementation(libs.moshi)

    // Util
    implementation(libs.timber)

    // Date
    implementation(libs.persianDate)

    // iText for pdf
    implementation(libs.iTextPdf)
    implementation(libs.lottieCompose)

    implementation(libs.gifDrawable)

    // firebase
    implementation(libs.firebaseAnalytics)
    implementation(libs.firebaseCrashlytics)
    implementation(libs.firebaseMessaging)

    // EncryptedSharedPreferences
    implementation(libs.androidxSecurityCrypto)

    implementation(libs.sentryAndroid)

    // ktor
    implementation(libs.ktorClientCore)
    implementation(libs.ktorClientOkHttp)

    // Balloon
    implementation(libs.ballonCompose)

    // google play service
    implementation(libs.googlePlayServiceAuth)
    implementation(libs.googlePlayServiceAuthApiPhone)

    implementation(libs.coilCompose)
}