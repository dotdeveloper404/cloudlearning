import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.dagger)
//    alias(libs.plugins.ksp)
//    alias(libs.plugins.safeargs)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.google.services)
//    alias(libs.plugins.serialization)
//    alias(libs.plugins.serialization.plugin)
    alias(libs.plugins.crashlytics)
//    alias(libs.plugins.firebase.perf)
    kotlin("kapt")

}

android {
    namespace = "com.sparkmembership.sparkowner"
    compileSdk = 35

    flavorDimensions += "version"
    productFlavors {

        create("dev"){
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "BASE_URL", "\"https://sparkwindevapi.azurewebsites.net/api/sparktan/\"")
        }

        create("prod"){
            versionNameSuffix = "-prod"
            buildConfigField("String", "BASE_URL", "\"https://com.sparkmembership.sparkowner/\"")

        }
        create("qa"){
            applicationIdSuffix = ".qa"
            versionNameSuffix = "-qa"
            buildConfigField("String", "BASE_URL", "\"https://sparkwinapiqa.azurewebsites.net/api/sparktan/\"")
        }
    }


    defaultConfig {
        applicationId = "com.sparktan"
        minSdk = 24
        targetSdk = 35
        versionCode = getCustomBuildVersion()
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }


    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


    buildFeatures {
        viewBinding =  true
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

// Retrofit and OkHttp
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.logging.interceptor)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)

    // Kotlin Coroutines
    implementation(libs.retrofit2.kotlin.coroutines.adapter)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Saved State Module for ViewModel/Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    kapt(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Jetpack Data Store
    implementation(libs.androidx.preference.ktx)

    //data-store proto-buff
    implementation( libs.androidx.datastore.preferences)
    implementation  (libs.androidx.datastore)
    implementation  (libs.protobuf.javalite)

    //Serializable
    implementation (libs.kotlinx.serialization.json)

    //piccaso for Image Loading
    implementation (libs.picasso)

    //for font size
    implementation (libs.sdp.android)
    implementation (libs.ssp.android)

    //firebase config
    implementation(platform(libs.firebase.bom))
    implementation (libs.firebase.messaging.ktx)
    implementation (libs.firebase.analytics.ktx)
    implementation (libs.firebase.config.ktx)
    implementation (libs.firebase.crashlytics.ktx)
    implementation (libs.firebase.auth)

    //ViewPager2
    implementation (libs.viewpager2)
    implementation (libs.androidx.constraintlayout)

    //circularimageview
    implementation(libs.circularimageview)

    implementation (libs.material)

    //imagePicker
    implementation (libs.imagepicker)

    //Location Services
    implementation(libs.play.services.location)

    //Pie Chart
    implementation(libs.pie.chart)
    implementation(libs.mpandroidchart)

    // Glide For ImageLoad
    implementation (libs.glide)


}

kapt {
    correctErrorTypes = true
}


fun getCustomBuildVersion(): Int {
    val date = Date()
    val formattedDate = SimpleDateFormat("yyMMdd").format(date)
    return formattedDate.toInt() + 1
}