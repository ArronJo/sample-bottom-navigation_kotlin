import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    //id("com.google.devtools.ksp")
}

val keystorePropertiesFile = rootProject.file("./keystore/keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.snc.sample.bottom_navigation_kotlin"

    compileSdk = rootProject.extra["target_sdk_version"] as Int
    compileSdkPreview = "UpsideDownCake"

    defaultConfig {
        applicationId = "com.snc.sample.bottom_navigation_kotlin"

        minSdk = rootProject. extra["min_sdk_version"] as Int
        targetSdk = rootProject.extra["target_sdk_version"] as Int

        versionCode = 10010013
        versionName = "1.1.13"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters.add("armeabi")
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
        }

        setProperty("archivesBaseName", "${applicationId}-${versionName}")
    }

    signingConfigs {

        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String

            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            //aaptOptions.cruncherEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["usesCleartextTrafficPermitted"] = "false"
        }
        //debug {
        //    signingConfig = signingConfigs.getByName("debug")
        //    isMinifyEnabled = false
        //    proguardFiles(
        //        getDefaultProguardFile("proguard-android-optimize.txt"),
        //        "proguard-rules.pro"
        //    )
        //    manifestPlaceholders["usesCleartextTrafficPermitted"] = "true"
        //}
    }

    flavorDimensions += "server"
    productFlavors {
        create("prod") {
            dimension = "server"
            manifestPlaceholders["applicationLabel"] = "@string/app_name"
            manifestPlaceholders["providerSuffixName"] = ""
            resValue("string", "flavors", "prod")
        }
        create("stage") {
            dimension = "server"
            applicationIdSuffix = ".stage"
            manifestPlaceholders["applicationLabel"] = "@string/app_name"
            manifestPlaceholders["providerSuffixName"] = "_stage"
            resValue("string", "flavors", "stage")
        }
        create("dev") {
            dimension = "server"
            applicationIdSuffix = ".dev"
            manifestPlaceholders["applicationLabel"] = "@string/app_name"
            manifestPlaceholders["providerSuffixName"] = "_dev"
            resValue("string", "flavors", "dev")
        }
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets/common")
            java.srcDirs("src/main/java")
            res.srcDirs("src/main/res/common")
        }
        getByName("dev") {
            assets.srcDirs("src/main/assets/common", "src/main/assets/dev")
            java.srcDirs("src/main/java", "src/dev/java")
            res.srcDirs("src/main/res/common", "src/main/res/dev")
        }
        getByName("stage") {
            assets.srcDirs("src/main/assets/common", "src/main/assets/stage")
            java.srcDirs("src/main/java", "src/stage/java")
            res.srcDirs("src/main/res/common", "src/main/res/stage")
        }
        getByName("prod") {
            assets.srcDirs("src/main/assets/common", "src/main/assets/prod")
            java.srcDirs("src/main/java", "src/prod/java")
            res.srcDirs("src/main/res/common", "src/main/res/prod")
        }
    }

    //sourceSets.all {// this: com.android.build.api.dsl.AndroidSourceSet ->
    //    println("sourceSet.name = \"${name}\"")
    //    // Not a kotlin.sequences.all call anymore so nothing to return
    //}

    buildFeatures {
        dataBinding = true
        buildConfig = true
        //compose = true
    }
    compileOptions {
        sourceCompatibility = rootProject.extra["java_compiler_version"] as JavaVersion
        targetCompatibility = rootProject.extra["java_compiler_version"] as JavaVersion
    }
    kotlinOptions {
        jvmTarget = rootProject.extra["java_compiler_version"].toString()
    }
    kotlin {
        jvmToolchain(rootProject.extra["jvm_toolchain"] as Int)
    }
    //composeOptions {
    //    kotlinCompilerExtensionVersion = "1.5.1"  // "1.4.3"
    //}

    packaging {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        resources.excludes.add("**/*.md")
    }
}

dependencies {

    implementation(project(":library-zero-lib-kotlin"))
    implementation(project(":library-zero-ui-kotlin"))
    implementation(project(":library-zero-resources"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.multidex)

    implementation(libs.log.timber)

    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.work.runtime)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.androidx.webkit)

    implementation(libs.toast.styleabletoast)

    //releaseImplementation(libs.chucker.no.op)
    //debugImplementation(libs.chucker)

    debugImplementation(libs.leakcanary.android)
}