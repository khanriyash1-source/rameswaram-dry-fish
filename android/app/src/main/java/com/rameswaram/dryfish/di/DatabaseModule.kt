package com.rameswaram.dryfish.di

import com.rameswaram.dryfish.data.local.AppDatabase
import com.rameswaram.dryfish.data.local.CartDao
import com.rameswaram.dryfish.data.local.ProductCacheDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getInstance(androidContext()) }
    single { provideCartDao(get()) }
    single { provideProductCacheDao(get()) }
}

fun provideCartDao(database: AppDatabase): CartDao = database.cartDao()

fun provideProductCacheDao(database: AppDatabase): ProductCacheDao = database.productCacheDao()
