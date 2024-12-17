plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
}

android {
    namespace = "unsa.etf.rma.rmaprojekat"
    compileSdk = 34
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "unsa.etf.rma.rmaprojekat"
        minSdk = 30
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
        debug{
            buildConfigField ("String", "TREFLE_API_KEY", project.properties["TREFLE_API_KEY"].toString())
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.games.text.input)
    implementation(libs.androidx.uiautomator)
    implementation(libs.play.services.base)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    implementation("androidx.camera:camera-core:1.2.2")
    implementation("androidx.camera:camera-camera2:1.2.2")
    implementation("androidx.camera:camera-lifecycle:1.2.2")
    implementation("androidx.camera:camera-video:1.2.2")

    implementation("androidx.camera:camera-view:1.2.2")
    implementation("androidx.camera:camera-extensions:1.2.2")
    implementation("com.google.guava:guava:30.1-android")
    testImplementation("junit:junit:4.13.2") 

    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    androidTestImplementation("androidx.test:core-ktx:1.5.0")

    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")

    implementation(files("libs/hamcrest-library-1.3.jar"))
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    implementation("com.squareup.retrofit2:retrofit:+")
    implementation("com.squareup.retrofit2:converter-gson:+")
    testImplementation ("org.assertj:assertj-core:3.22.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    implementation("androidx.room:room-runtime:+")
    annotationProcessor("androidx.room:room-compiler:+")
    implementation("androidx.room:room-ktx:+")
    kapt("androidx.room:room-compiler:+")
}