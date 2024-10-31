# opticon_h35_sdk

This is package for flutter to communicate between h35 SDK Scanner and flutter app

## Release Note
- Need setup **Proguard Settings** for Android release

- Add this config for **Release buildTypes** in `android/app/build.gradle`

```agsl
buildTypes {
    release {
        // TODO: Add your own signing config for the release build.
        // Signing with the debug keys for now, so `flutter run --release` works.
        signingConfig = signingConfigs.release

        minifyEnabled true
        shrinkResources true

        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

- Add file `android/app/proguard-rules.pro` with content:

```agsl
# Keep your jar package
-keep class com.opticon.package.** { *; }
-keepclassmembers class com.opticon.package.** { *; }

# Keep all classes in your jar
-keep class **.** { *; }
-keepattributes *Annotation*
-dontwarn **
-keep class androidx.** { *; }

# If you're using reflection
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep the entry points
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep enum
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
```

