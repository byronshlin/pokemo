plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.byronlin.pokemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.byronlin.pokemo"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    kapt("com.github.bumptech.glide:compiler:4.11.0")


    //network
    val retrofit_version = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:${retrofit_version}")
    // Retrofit converter-gson for json conversion
    implementation("com.squareup.retrofit2:converter-gson:${retrofit_version}")
    implementation("com.squareup.retrofit2:adapter-rxjava3:$retrofit_version")



    val okhttp_version ="4.10.0"
    implementation("com.squareup.okhttp3:okhttp:${okhttp_version}")
    implementation("com.squareup.okhttp3:okhttp:${okhttp_version}")
    testImplementation("com.squareup.okhttp3:mockwebserver:${okhttp_version}")
    implementation("com.squareup.okhttp3:logging-interceptor:${okhttp_version}")

    //API
    // CoRoutine
    val coroutine_version ="1.6.4"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutine_version}")

    // RxJava
    val rxandroid_version ="3.0.2"
    val rxjava_version ="3.1.5"
    implementation("io.reactivex.rxjava3:rxandroid:${rxandroid_version}")
    implementation("io.reactivex.rxjava3:rxjava:${rxjava_version}")


    val room_version = "2.4.2"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version") // use kapt for Kotlin
    testImplementation("androidx.room:room-testing:$room_version")
    implementation("androidx.room:room-guava:$room_version")





    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.10.3")

    testImplementation("androidx.test.ext:junit:1.1.2")
    testImplementation("androidx.test.ext:truth:1.3.0")
    testImplementation("com.google.truth:truth:1.1.3")

    androidTestImplementation("androidx.test:core:1.0.0")
    androidTestImplementation("androidx.test:runner:1.1.0")
    androidTestImplementation("androidx.test:rules:1.1.0")
    // Optional -- UI testing with Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.0")
    // Optional -- UI testing with UI Automator
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    // Optional -- Hamcrest library
    androidTestImplementation("org.hamcrest:hamcrest-library:1.3")

    testImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")

    // Assertions
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.ext:truth:1.3.0")
    androidTestImplementation("com.google.truth:truth:1.1.3")

    //Mockito
    testImplementation("org.mockito:mockito-core:3.2.0")
    //Mockk for Kotlin
    testImplementation("io.mockk:mockk:1.12.4")
}