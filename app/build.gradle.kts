plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.dropshep.bdhelper"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dropshep.bdhelper"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.firestore)

    //image Slider
    implementation(libs.image.slider)

    //Google maps activity
    implementation (libs.google.play.services.maps)
    implementation (libs.google.play.services.location)
    implementation (libs.material.searchbar)
    implementation (libs.google.places)

    //Image View And Compiler
    implementation(libs.picasso)
    implementation(libs.glide)

    //Retrofit
    implementation (libs.retrofit)
    implementation (libs.gson.converter)
    implementation (libs.okhttp.logging.interceptor)

    //Image Cropper
    implementation(libs.ucrop.imagecropper)
    implementation(libs.play.services.auth)

    //Lottie
    implementation(libs.lottie)

    //wheel picker
    /*implementation("cn.iwgang:wheelpicker:1.5.6")
    implementation("io.github.darkokoa:datetime-wheel-picker-android:1.0.1")*/



testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}