import org.gradle.kotlin.dsl.annotationProcessor
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.note"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.note"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/\"")
            // 如需允许明文（可选）：isMinifyEnabled = false
        }
        release {
            //服务器url
            buildConfigField("String", "BASE_URL", "\"https://api.yourdomain.com/\"")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        dataBinding = true      // ← 开启 DataBinding
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.4.0")

    //老版lifecycle，可以升级到lifecycle-livedata-ktx / lifecycle-viewmodel-ktx
    api("androidx.lifecycle:lifecycle-extensions:2.2.0")

    //网络请求框架
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.okhttp3:okhttp:4.11.0")
    api("com.google.code.gson:gson:2.11.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")

    //建议升级到 com.squareup.okhttp3:logging-interceptor:4.11.0 ，跟上面okhttp3统一
    implementation("com.squareup.okhttp3:logging-interceptor:3.4.1")

    //时间选择器
    implementation("com.github.loperSeven:DateTimePicker:0.6.3")

    //图片/头像
    implementation("jp.co.cyberagent.android.gpuimage:gpuimage-library:1.3.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.activity)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

}