# 奥数闯关王 - ProGuard 规则

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep class * { @androidx.room.* <fields>; }

# Hilt
-keep class * extends dagger.hilt.** { *; }
-keepclassmembers class * {
    @dagger.* <methods>;
}

# Gson
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.** { *; }
-keep class * { @com.google.gson.annotations.SerializedName <fields>; }

# 数据模型
-keep class com.example.matholympiad.data.local.model.** { *; }
-keep class com.example.matholympiad.domain.model.** { *; }

# 防止反射问题
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes SourceFile

# 保留行号用于崩溃日志
-renamesourcefileattribute SourceFile
-keepattributes LineNumberTable
