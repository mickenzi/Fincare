plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.safeargs)
    id("kotlin-kapt")
    kotlin("kapt")
}

android {
    namespace = "com.example.fincare"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fincare"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "DB", "\"fincaredb\"")
        }

        debug {
            isMinifyEnabled = false
            buildConfigField("String", "DB", "\"fincaredb\"")
        }

        create("staging") {
            initWith(getByName("debug"))
            isMinifyEnabled = false
            buildConfigField("String", "DB", "\"fincaredb\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    // Androidx
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.swiperefresh)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.constraintlayout)

    // Hilt
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hilt.compiler)

    // Material
    implementation(libs.material)

    // Calendar
    implementation(libs.calendar)

    // Charts
    implementation(libs.vico.core)
    implementation(libs.vico.views)

    // RxJava
    implementation(libs.rx.java)
    implementation(libs.rx.android)
    implementation(libs.rx.kotlin)

    // Room
    implementation(libs.room)
    implementation(libs.room.rxjava)
    annotationProcessor(libs.room.compiler)
    kapt(libs.room.compiler)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}

ktlint {
    android = true
    enableExperimentalRules = true
    ignoreFailures = false
}