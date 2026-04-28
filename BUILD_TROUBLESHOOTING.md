# 构建问题排查指南

## 问题现象
运行 `./gradlew :app:assembleRelease` 时出现错误：
```
> Task :app:kaptGenerateStubsReleaseKotlin FAILED
e: Could not load module <Error module>
```

## 环境信息
- Kotlin: 1.9.20
- Gradle: 8.2
- Compose Compiler: 1.5.8
- Room: 2.6.0
- Hilt: 2.48
- JDK: 17.0.18

## 已尝试的修复
1. ✅ 更新 Compose Compiler 从 1.5.4 → 1.5.8
2. ✅ 添加 Kotlin 编译器参数抑制版本检查
3. ✅ 配置 KAPT javacOptions 使用 Java 17
4. ✅ 清理 Gradle 缓存和构建目录

## 推荐解决方案

### 方案1：降级 Kotlin 版本（推荐）
修改 `build.gradle.kts` (项目根目录)：
```kotlin
plugins {
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}
```

修改 `app/build.gradle.kts`：
```kotlin
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.1"
}
```

### 方案2：升级 Gradle 和 Kotlin
修改 `gradle/wrapper/gradle-wrapper.properties`：
```
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
```

修改 `build.gradle.kts`：
```kotlin
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
}
```

移除 `composeOptions` 块（Kotlin 2.0 使用 Compose Compiler Plugin）

### 方案3：使用 Android Studio 构建
1. 打开项目于 Android Studio
2. Build → Generate Signed Bundle/APK
3. 选择 APK → 选择 release-key.jks
4. 完成构建

## 验证步骤
```bash
# 清理并重新构建
./gradlew clean
./gradlew :app:assembleRelease
```

## 成功标志
```
BUILD SUCCESSFUL in XXs
56 actionable tasks: 56 executed
```

APK 输出位置：`app/build/outputs/apk/release/app-release.apk`
