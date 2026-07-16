package com.rameswaram.dryfish

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.firebase.FirebaseApp
import com.rameswaram.dryfish.data.api.RetrofitClient
import com.rameswaram.dryfish.di.appModule
import com.rameswaram.dryfish.di.databaseModule
import com.rameswaram.dryfish.di.networkModule
import com.rameswaram.dryfish.utils.Constants
import com.razorpay.Checkout
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.Locale

class RameswaramApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        RetrofitClient.init(this)

        applySavedLocale()

        Checkout.preload(this)

        startKoin {
            androidContext(this@RameswaramApp)
            modules(
                networkModule,
                databaseModule,
                appModule
            )
        }
    }

    private fun applySavedLocale() {
        val isTamil = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean("isTamilLanguage", false)
        if (isTamil) {
            val locale = Locale("ta")
            Locale.setDefault(locale)
            val config = Configuration(resources.configuration)
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient(RetrofitClient.okHttpClient)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_cache"))
                    .maxSizeBytes(50 * 1024 * 1024)
                    .build()
            }
            .crossfade(false)
            .build()
    }
}
