# ProGuard rules for Math Olympiad app
# Keep Hilt classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Room entities
-keep class com.example.matholympiad.data.local.model.** { *; }