import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.unh.anyscanner_rajat_rohith_f23"
    compileSdk = 33

    packaging {
        resources {
            excludes +=("META-INF/NOTICE.md")
            excludes +=("META-INF/LICENSE.md")
        }
    }
    defaultConfig {
        applicationId = "com.unh.anyscanner_rajat_rohith_f23"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        //buildConfigField("String","default_web_client_id","default_web_client_id")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    //googles method of variable binding - links kt to xml
    buildFeatures{
        viewBinding=true
        buildConfig=true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.9.1")
    //third party lib for camera based on ZXing
    implementation("com.github.yuriy-budiyev:code-scanner:2.3.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation ("com.sun.mail:android-activation:1.6.7")
    implementation ("androidx.biometric:biometric:1.1.0")
    implementation ("androidx.work:work-runtime:2.7.1")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation ("com.scottyab:rootbeer-lib:0.1.0")

}

