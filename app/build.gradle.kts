import java.util.Properties

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
        versionCode = 7
        versionName = "1.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // local.properties থেকে API Key লোড করা
        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { properties.load(it) }
        }
        val mapsApiKey = properties.getProperty("MAPS_API_KEY") ?: ""
        manifestPlaceholders["mapsApiKey"] = mapsApiKey
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

    bundle {
        language {
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

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
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

    implementation(libs.google.play.services.maps)
    implementation(libs.google.play.services.location)
    implementation(libs.material.searchbar)
    implementation(libs.google.places)

    //Image View And Compiler
    implementation(libs.picasso)
    implementation(libs.glide)

    implementation(libs.retrofit)
    implementation(libs.gson.converter)
    implementation(libs.okhttp.logging.interceptor)

    //Image Cropper
    implementation(libs.imageprocessor)

    //Lottie
    implementation(libs.lottie)

    //Loading Dialog
    implementation(libs.loading.dialog)

    implementation(libs.app.update)
    implementation(libs.app.update.ktx)
    
    // Room components
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
