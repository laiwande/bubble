# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Retrofit and Gson classes
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

# Keep Retrofit API interfaces
-keep interface com.bubble.network.** { *; }

# Keep Gson model classes
-keep class com.bubble.model.** { *; }

-dontwarn retrofit2.**
-dontwarn okio.**
