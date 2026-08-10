# Vosk / JNA
-keep class org.vosk.** { *; }
-keep class com.sun.jna.** { *; }
-keepclassmembers class * extends com.sun.jna.Structure {
    <fields>;
}
-dontwarn java.awt.**

# ML Kit Translate + Language ID 17+
-keep class com.google.mlkit.nl.translate.** { *; }
-keep class com.google.mlkit.nl.languageid.** { *; }
-keep class com.google.mlkit.nl.languageid.internal.ThickLanguageIdentifier { *; }
-dontwarn com.google.mlkit.**
