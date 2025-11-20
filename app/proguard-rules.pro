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

# --- Retrofit --- 
-dontwarn retrofit2.Platform$Java8
-keep interface ** { @retrofit2.http.* <methods>; }

# --- Google Maps and Places --- 
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }
-keep class com.google.android.libraries.places.** { *; }
-keep interface com.google.android.libraries.places.** { *; }

# --- Glide --- 
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$ImageType

# --- General --- 
# Keep default constructors for classes that are instantiated reflectively 
-keepclassmembers class * { 
    public <init>(); 
}
