# Project specific ProGuard / R8 rules for NanasAi

# 1. Pertahankan semua kelas aplikasi NanasAi
-keep class com.adnan.nanasai.** { *; }

# 2. Pertahankan metode native C++ (JNI) agar tidak di-obfuscate / dihapus
-keepclasseswithmembernames class * {
    native <methods>;
}

# 3. Google ML Kit Vision & Text Recognition
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# 4. Kotlin Coroutines & Reflection
-keepattributes *Annotation*,InnerClasses,EnclosingMethod,Signature,SourceFile,LineNumberTable
-dontwarn kotlinx.coroutines.**
