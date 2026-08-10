# Vosk / JNA
-keep class org.vosk.** { *; }
-keep class com.sun.jna.** { *; }
-keepclassmembers class * extends com.sun.jna.Structure {
    <fields>;
}
-dontwarn java.awt.**

# Firebase Components DI used by ML Kit (AGP 9 / R8 full mode)
-dontwarn com.google.firebase.components.Component$Instantiation
-dontwarn com.google.firebase.components.Component$ComponentType
-keep class * implements com.google.firebase.components.ComponentRegistrar {
  void <init>();
}
-keep,allowshrinking interface com.google.firebase.components.ComponentRegistrar

# ML Kit common + NL (translate / language-id)
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_** { *; }
-dontwarn com.google.mlkit.**
