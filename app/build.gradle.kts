import org.gradle.api.tasks.testing.Test
import org.gradle.process.CommandLineArgumentProvider

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.sanaescencia"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.sanaescencia"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            optimization {
                enable = false
            }
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all { test ->
                test.systemProperty("robolectric.graphicsMode", "NATIVE")
                // JUnit Platform ejecuta las pruebas JUnit 5/Kotest y, a traves
                // del motor Vintage, las pruebas JUnit 4 (Robolectric y Compose).
                test.useJUnitPlatform()
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
    }
}

// Robolectric define las clases de la aplicacion en su propio classloader sin
// CodeSource, por lo que JaCoCo las ignora por defecto. AGP fija
// inclnolocationclasses=false; aqui se reemplaza el argumento del agente para
// que las pruebas de UI (Robolectric/Compose) tambien aporten cobertura.
tasks.withType<Test>().configureEach {
    doFirst {
        val proveedores = jvmArgumentProviders
        val proveedoresJacoco = proveedores.filter { proveedor ->
            runCatching {
                proveedor.asArguments().any { it.contains("jacocoagent", ignoreCase = true) }
            }.getOrDefault(false)
        }
        if (proveedoresJacoco.isNotEmpty()) {
            val argumentosCorregidos = proveedoresJacoco
                .flatMap { it.asArguments() }
                .filter { it.contains("jacocoagent", ignoreCase = true) }
                .map { it.replace("inclnolocationclasses=false", "inclnolocationclasses=true") }
            proveedores.removeAll(proveedoresJacoco.toSet())
            proveedores.add(CommandLineArgumentProvider { argumentosCorregidos })
            logger.lifecycle("JaCoCo: inclnolocationclasses habilitado en $name")
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.9.6")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")

    // Conversión JSON con Gson
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // Corrutinas para Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // CameraX
    implementation("androidx.camera:camera-core:1.6.2")
    implementation("androidx.camera:camera-camera2:1.6.2")
    implementation("androidx.camera:camera-lifecycle:1.6.2")
    implementation("androidx.camera:camera-view:1.6.2")
    implementation("androidx.exifinterface:exifinterface:1.4.2")

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.androidx.junit)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)

    // JUnit 5 + Kotest 5.9.0 + MockK 1.13.10
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.vintage.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.mockk)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
}