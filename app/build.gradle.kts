plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "pt.ipleiria.estg.dei.coworkipleiria_02"
    compileSdk = 36

    defaultConfig {
        applicationId = "pt.ipleiria.estg.dei.coworkipleiria_02"
        minSdk = 24
        targetSdk = 36
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // ZXing para QR Code (já tens)
    implementation("com.google.zxing:core:3.5.3")

    // Room - Banco de dados local (SQLite) - ESSENCIAL
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")  // <--- ESTA LINHA RESOLVE O ERRO

    // Opcional: suporte moderno (LiveData/Flow) - pode remover se não usar
    implementation("androidx.room:room-ktx:2.6.1")

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}