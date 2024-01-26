// Define versions in a single place
buildscript {
    extra.apply {
        set("java_compiler_version", JavaVersion.VERSION_17)
        set("jvm_toolchain", 17)
        set("min_sdk_version", 23)
        set("target_sdk_version", 34)
    }
    repositories {
        // Make sure that you have the following two repositories
        google()  // Google's Maven repository

        mavenCentral()  // Maven Central repository
    }
    dependencies {

    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.detekt.android) apply false
}