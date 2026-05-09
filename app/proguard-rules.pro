# Add project specific ProGuard rules here.
# For more details, see http://developer.android.com/guide/developing/tools/proguard.html

# Keep line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# --- GSON ---
-keepattributes Signature, *Annotation*
-keep class com.google.gson.annotations.** { *; }
-keep class com.krishibarirangpur.bdhelper.model.** { *; }
-keep class com.krishibarirangpur.bdhelper.api.** { *; }
-keepclassmembers class com.krishibarirangpur.bdhelper.model.** { <fields>; }

# --- Retrofit & OkHttp ---
-keepattributes Signature, InnerClasses, EnclosingMethod
-dontwarn retrofit2.Platform$Java8
-keep interface ** { @retrofit2.http.* <methods>; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# --- Google Maps and Places ---
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }
-keep class com.google.android.libraries.places.** { *; }
-keep interface com.google.android.libraries.places.** { *; }

# --- Glide ---
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$ImageType
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

# --- Lottie ---
-keep class com.airbnb.lottie.** { *; }

# --- General ---
# Keep default constructors for classes that are instantiated reflectively
-keepclassmembers class * {
    public <init>();
}

# --- R8 Missing Class Warning Fix ---
-dontwarn java.sql.JDBCType
-dontwarn javax.lang.model.**
-dontwarn org.conscrypt.**
