package com.rameswaram.dryfish.data.api

import com.rameswaram.dryfish.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("sort") sort: String? = null
    ): Response<ApiResponse<List<Product>>>

    @GET("products/featured")
    suspend fun getFeaturedProducts(): Response<ApiResponse<List<Product>>>

    @GET("products/bestsellers")
    suspend fun getBestsellers(): Response<ApiResponse<List<Product>>>

    @GET("products/{slug}")
    suspend fun getProductBySlug(
        @Path("slug") slug: String
    ): Response<ApiResponse<Product>>

    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<Category>>>

    @GET("cart")
    suspend fun getCart(): Response<ApiResponse<List<CartItem>>>

    @POST("cart")
    suspend fun addToCart(@Body item: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<CartItem>>

    @PUT("cart/{id}")
    suspend fun updateCartItem(
        @Path("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiResponse<CartItem>>

    @DELETE("cart/{id}")
    suspend fun removeFromCart(@Path("id") id: String): Response<ApiResponse<Unit>>

    @POST("orders")
    suspend fun createOrder(@Body order: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<Order>>

    @GET("orders")
    suspend fun getOrders(): Response<ApiResponse<List<Order>>>

    @GET("orders/{id}")
    suspend fun getOrderDetails(@Path("id") id: String): Response<ApiResponse<Order>>

    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<ApiResponse<User>>

    @POST("auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<ApiResponse<User>>

    @POST("auth/google")
    suspend fun googleLogin(@Body body: Map<String, String>): Response<ApiResponse<User>>

    @GET("wishlist")
    suspend fun getWishlist(): Response<ApiResponse<List<WishlistItem>>>

    @POST("wishlist")
    suspend fun addToWishlist(@Body body: Map<String, String>): Response<ApiResponse<WishlistItem>>

    @DELETE("wishlist/{productId}")
    suspend fun removeFromWishlist(@Path("productId") productId: String): Response<ApiResponse<Unit>>

    @GET("addresses")
    suspend fun getAddresses(): Response<ApiResponse<List<Address>>>

    @POST("addresses")
    suspend fun addAddress(@Body address: Address): Response<ApiResponse<Address>>

    @PUT("addresses/{id}")
    suspend fun updateAddress(
        @Path("id") id: String,
        @Body address: Address
    ): Response<ApiResponse<Address>>

    @DELETE("addresses/{id}")
    suspend fun deleteAddress(@Path("id") id: String): Response<ApiResponse<Unit>>
}
