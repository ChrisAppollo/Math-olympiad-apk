plugins {
 id("com.android.application")
 id("org.jetbrains.kotlin.android")
 id("com.google.devtools.ksp")
 id("com.google.dagger.hilt.android")
}

android {
  namespace = "com.example.matholympiad"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.matholympiad"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  signingConfigs {
    create("release") {
      // 从环境变量或 Gradle 属性读取密钥信息（GitHub Actions 使用）
      val keystorePassword = System.getenv("KEYSTORE_PASSWORD") ?: project.findProperty("KEYSTORE_PASSWORD") as String? ?: "math-app-2026"
      val keyAlias = System.getenv("KEY_ALIAS") ?: project.findProperty("KEY_ALIAS") as String? ?: "matholympiad"
      val keyPassword = System.getenv("KEY_PASSWORD") ?: project.findProperty("KEY_PASSWORD") as String? ?: "math-app-2026"
      
      storeFile = file("release-key.jks")
      storePassword = keystorePassword
      keyAlias = keyAlias
      keyPassword = keyPassword
    }
  }

 buildTypes {
 release {
 isMinifyEnabled = true
 isShrinkResources = true
 proguardFiles(
 getDefaultProguardFile("proguard-android-optimize.txt"),
 "proguard-rules.pro"
 )
 signingConfig = signingConfigs.getByName("release")
 }
 debug {
 isDebuggable = true
 applicationIdSuffix = ".debug"
 }
 }
 compileOptions {
 sourceCompatibility = JavaVersion.VERSION_17
 targetCompatibility = JavaVersion.VERSION_17
 }
 kotlinOptions {
 jvmTarget = "17"
 }
 buildFeatures {
 compose = true
 }
 composeOptions {
 // Kotlin 1.9.24 需要 Compose Compiler 1.5.14
 kotlinCompilerExtensionVersion = "1.5.14"
 }
 packaging {
 resources {
 excludes += "/META-INF/{AL2.0,LGPL2.1}"
 }
 }
}

dependencies {
 // Gson for JSON conversion
 implementation("com.google.code.gson:gson:2.10.1")
 // Core Android
 implementation("androidx.core:core-ktx:1.12.0")
 implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
 implementation("androidx.activity:activity-compose:1.8.1")
 implementation(platform("androidx.compose:compose-bom:2023.10.01"))
 implementation("androidx.compose.ui:ui")
 implementation("androidx.compose.ui:ui-graphics")
 implementation("androidx.compose.ui:ui-tooling-preview")
 implementation("androidx.compose.material3:material3")

 // Room Database - 使用 KSP 替代 KAPT
 implementation("androidx.room:room-runtime:2.6.0")
 implementation("androidx.room:room-ktx:2.6.0")
 ksp("androidx.room:room-compiler:2.6.0")

 // Hilt Dependency Injection - 使用 KSP
 implementation("com.google.dagger:hilt-android:2.48")
 ksp("com.google.dagger:hilt-compiler:2.48")
 implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

 // Coroutines
 implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
 
 // Lottie Animations
 implementation("com.airbnb.android:lottie-compose:6.2.0")
 
 // Testing - JUnit 5
 testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
 testRuntimeOnly("org.junit.platform:junit-platform-launcher")

 // MockK for Kotlin mocking
 testImplementation("io.mockk:mockk:1.13.8")

 // Coroutines Test
 testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

 // Hilt Test - 测试仍使用 kapt
 testImplementation("com.google.dagger:hilt-android-testing:2.48")
 kspTest("com.google.dagger:hilt-compiler:2.48")

 // Architecture Components Test
 testImplementation("androidx.arch.core:core-testing:2.2.0")

 // Android Testing
 androidTestImplementation("androidx.test.ext:junit:1.1.5")
 androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
 androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
 androidTestImplementation("androidx.compose.ui:ui-test-junit4")
 debugImplementation("androidx.compose.ui:ui-tooling")
 debugImplementation("androidx.compose.ui:ui-test-manifest")
}
