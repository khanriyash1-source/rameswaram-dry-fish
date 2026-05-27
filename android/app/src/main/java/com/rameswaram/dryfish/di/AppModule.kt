package com.rameswaram.dryfish.di

import com.google.firebase.auth.FirebaseAuth
import com.rameswaram.dryfish.data.repository.*
import com.rameswaram.dryfish.presentation.auth.AuthViewModel
import com.rameswaram.dryfish.presentation.cart.CartViewModel
import com.rameswaram.dryfish.presentation.checkout.CheckoutViewModel
import com.rameswaram.dryfish.presentation.home.HomeViewModel
import com.rameswaram.dryfish.presentation.orders.OrdersViewModel
import com.rameswaram.dryfish.presentation.product.ProductDetailViewModel
import com.rameswaram.dryfish.presentation.profile.ProfileViewModel
import com.rameswaram.dryfish.presentation.shop.ShopViewModel
import com.rameswaram.dryfish.presentation.wishlist.WishlistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }

    single { AuthRepository(androidContext(), get(), get()) }
    single { ProductRepository(get(), get()) }
    single { CartRepository(get(), get()) }
    single { OrderRepository(get()) }
    single { WishlistRepository(get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ShopViewModel(get()) }
    viewModel { ProductDetailViewModel(get()) }
    viewModel { CartViewModel(get()) }
    viewModel { CheckoutViewModel(get(), get(), get()) }
    viewModel { OrdersViewModel(get()) }
    viewModel { WishlistViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
}
