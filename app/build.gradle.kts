plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.krishibarirangpur.bdhelper"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.krishibarirangpur.bdhelper"
        minSdk = 28
        targetSdk = 36
        versionCode = 6
        versionName = "1.0.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
    }

    bundle {
        language {
            // Disable language splits, so all languages stay in the base APK
            enableSplit = false
        }
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
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    ndkVersion = "29.0.14206865"
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)

    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)

    implementation(libs.firebase.messaging)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.firestore)

    //image Slider
    implementation(libs.image.slider)

    //slider
    implementation(libs.autoimageslider)
    //Swipe Decorator
    implementation(libs.swipe.decorator)

    //Google Maps activity
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
    implementation(libs.imageprocessor)

    //Lottie
    implementation(libs.lottie)

    //Loading Dialog
    implementation(libs.loading.dialog)

    implementation(libs.app.update)
    implementation(libs.app.update.ktx)


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}