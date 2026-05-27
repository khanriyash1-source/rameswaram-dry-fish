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

# Keep Firebase
-keep class com.google.firebase.** { *; }

# Keep Razorpay
-keep class com.razorpay.** { *; }
-dontwarn com.razorpay.**
