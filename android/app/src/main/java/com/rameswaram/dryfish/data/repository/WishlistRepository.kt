package com.rameswaram.dryfish.data.repository

import com.rameswaram.dryfish.data.api.ApiService
import com.rameswaram.dryfish.domain.model.WishlistItem
import com.rameswaram.dryfish.utils.Resource

class WishlistRepository(
    private val apiService: ApiService
) {
    suspend fun getWishlist(): Resource<List<WishlistItem>> {
        return try {
            val response = apiService.getWishlist()
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data ?: emptyList())
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load wishlist")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun addToWishlist(productId: String): Resource<WishlistItem> {
        return try {
            val response = apiService.addToWishlist(mapOf("productId" to productId))
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to add to wishlist")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun removeFromWishlist(productId: String): Resource<Unit> {
        return try {
            val response = apiService.removeFromWishlist(productId)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to remove from wishlist")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}
