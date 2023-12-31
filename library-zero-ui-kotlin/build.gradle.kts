plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace  = "com.snc.zero.ui.kotlin"
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
    implementation(project(":library-zero-lib-kotlin"))
    implementation(project(":library-zero-resources"))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("com.airbnb.android:lottie:6.1.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")
}