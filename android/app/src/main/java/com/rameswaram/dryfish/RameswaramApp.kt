package com.rameswaram.dryfish

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.firebase.FirebaseApp
import com.rameswaram.dryfish.data.api.RetrofitClient
import com.rameswaram.dryfish.di.appModule
import com.rameswaram.dryfish.di.databaseModule
import com.rameswaram.dryfish.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RameswaramApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        RetrofitClient.init(this)

        startKoin {
            androidContext(this@RameswaramApp)
            modules(
                networkModule,
                databaseModule,
                appModule
            )
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
            .crossfade(true)
            .build()
    }
}
