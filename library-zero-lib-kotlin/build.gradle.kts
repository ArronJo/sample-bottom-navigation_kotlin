plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.snc.zero.lib.kotlin"
    compileSdk = rootProject.extra["target_sdk_version"] as Int
    compileSdkPreview = "UpsideDownCake"

    defaultConfig {
        minSdk = rootProject.extra["min_sdk_version"] as Int
        //targetSdk = rootProject.extra["target_sdk_version"] as Int

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = rootProject.extra["java_compiler_version"] as JavaVersion
        targetCompatibility = rootProject.extra["java_compiler_version"] as JavaVersion
    }
    kotlin {
        jvmToolchain(rootProject.extra["jvm_toolchain"] as Int)
    }
    kotlinOptions {
        jvmTarget = rootProject.extra["java_compiler_version"].toString()
    }
}

dependencies {
    //implementation(project(path: ":library-zero-androidx-kotlin"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.log.timber)

    implementation(libs.permission.ted)
    implementation(libs.permission.ted.normal)
}