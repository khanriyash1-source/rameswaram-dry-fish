package com.rameswaram.dryfish.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rameswaram.dryfish.data.local.LocalProductDataSource
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
    single { FirebaseFirestore.getInstance() }

    single { LocalProductDataSource(androidContext()) }
    single { FirestoreRepository(get()) }
    single { AuthRepository(androidContext(), get(), get(), get()) }
    single { ProductRepository(get()) }
    single { CartRepository(get(), get(), get()) }
    single { OrderRepository(get(), get(), get()) }
    single { WishlistRepository(get(), get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ShopViewModel(get(), get()) }
    viewModel { ProductDetailViewModel(get(), get(), get()) }
    viewModel { CartViewModel(get()) }
    viewModel { CheckoutViewModel(get(), get(), get(), get()) }
    viewModel { OrdersViewModel(get()) }
    viewModel { WishlistViewModel(get()) }
    viewModel { ProfileViewModel(androidContext(), get(), get()) }
}
