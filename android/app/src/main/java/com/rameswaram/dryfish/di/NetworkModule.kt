package com.rameswaram.dryfish.di

import com.rameswaram.dryfish.data.api.ApiService
import com.rameswaram.dryfish.data.api.RetrofitClient
import org.koin.dsl.module

val networkModule = module {
    single { RetrofitClient.getApiService() }
}
