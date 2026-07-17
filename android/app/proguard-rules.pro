# Keep Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep Gson
-keep class com.rameswaram.dryfish.domain.model.** { *; }
-keep class com.rameswaram.dryfish.data.local.** { *; }
-keep class com.rameswaram.dryfish.data.remote.** { *; }
-keep class com.rameswaram.dryfish.data.repository.** { *; }
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Keep Firebase
-keep class com.google.firebase.** { *; }

# Keep Razorpay
-keep class com.razorpay.** { *; }
-keep class * extends com.razorpay.** { *; }
-keep class * implements com.razorpay.** { *; }
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-dontwarn com.razorpay.**

# Keep Kotlin Metadata
-keepattributes KotlinInternal
-keep class kotlin.** { *; }

# Keep Room
-keep class * extends androidx.room.** { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Keep Compose
-dontwarn androidx.compose.**

# Keep Coil
-dontwarn coil.**

# Keep Koin
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Keep Google Play Services Auth
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.common.api.** { *; }
-keep class com.google.android.gms.signin.** { *; }
