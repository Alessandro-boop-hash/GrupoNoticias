plugins {
    // AGP 9 incluye el soporte de Kotlin de forma nativa, no se requiere
    // el plugin 'org.jetbrains.kotlin.android'.
    id("com.android.application")
}

android {
    namespace = "com.cibertec.noticias"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.cibertec.noticias"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Se firma con la llave de depuracion para que el APK de entrega
            // pueda instalarse directamente en un dispositivo.
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }

    lint {
        // PropertyEscape: local.properties lo regenera Android Studio en cada
        // maquina con rutas propias y no se versiona con el proyecto.
        disable += "PropertyEscape"
    }

    testOptions {
        unitTests {
            // Permite ejecutar las pruebas de interfaz sobre la JVM.
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // Pruebas de interfaz ejecutadas sobre la JVM con Robolectric.
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.16.1")
    testImplementation("androidx.test:core-ktx:1.6.1")
    testImplementation("androidx.test.ext:junit:1.2.1")
}