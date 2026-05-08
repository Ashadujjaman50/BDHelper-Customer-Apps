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

# --- R8 Missing Class Warning Fix ---
-dontwarn java.sql.JDBCType
-dontwarn javax.lang.model.SourceVersion
-dontwarn javax.lang.model.element.Element
-dontwarn javax.lang.model.element.ElementKind
-dontwarn javax.lang.model.element.ElementVisitor
-dontwarn javax.lang.model.element.ExecutableElement
-dontwarn javax.lang.model.element.Name
-dontwarn javax.lang.model.element.PackageElement
-dontwarn javax.lang.model.element.TypeElement
-dontwarn javax.lang.model.element.TypeParameterElement
-dontwarn javax.lang.model.element.VariableElement
-dontwarn javax.lang.model.type.ArrayType
-dontwarn javax.lang.model.type.DeclaredType
-dontwarn javax.lang.model.type.ExecutableType
-dontwarn javax.lang.model.type.TypeKind
-dontwarn javax.lang.model.type.TypeMirror
-dontwarn javax.lang.model.type.TypeVariable
-dontwarn javax.lang.model.type.TypeVisitor
-dontwarn javax.lang.model.util.ElementFilter
-dontwarn javax.lang.model.util.SimpleElementVisitor8
-dontwarn javax.lang.model.util.SimpleTypeVisitor8
-dontwarn javax.lang.model.util.Types