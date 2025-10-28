# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep all classes in the model package
-keep class com.dropshep.bdhelper.model.** { *; }

# Keep Barikoi API response classes
-keep class com.dropshep.bdhelper.api.** { *; }

# Keep classes for GSON serialization
-keepattributes Signature

# For Retrofit
-keep class retrofit2.** { *; }
-keep interface ** { @retrofit2.http.* <methods>; }

# Keep any class that is annotated with @SerializedName
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep data classes
-keepclassmembers class * extends java.lang.Object {
   public <init>(...);
}

# Google Maps and Places
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }
-keep class com.google.android.libraries.places.** { *; }
-keep interface com.google.android.libraries.places.** { *; }
