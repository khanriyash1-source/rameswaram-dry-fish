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
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Keep Firebase
-keep class com.google.firebase.** { *; }

# Keep Razorpay
-keep class com.razorpay.** { *; }
-keep class * extends com.razorpay.** { *; }
-keep class * implements com.razorpay.** { *; }
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-optimizations !class/merging/*,!code/allocation/variable
-dontwarn com.razorpay.**
