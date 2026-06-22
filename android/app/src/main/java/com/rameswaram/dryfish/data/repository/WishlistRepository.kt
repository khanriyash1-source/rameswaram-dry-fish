package com.rameswaram.dryfish.data.repository

import com.rameswaram.dryfish.domain.model.WishlistItem
import com.rameswaram.dryfish.utils.Resource

class WishlistRepository(
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) {
    suspend fun getWishlist(): Resource<List<WishlistItem>> {
        val uid = authRepository.getSavedUid() ?: return Resource.Error("Not logged in")
        return firestoreRepository.loadWishlist(uid)
    }

    suspend fun addToWishlist(
        productId: String,
        productName: String,
        productImage: String,
        productSlug: String,
        price: Double,
        mrp: Double,
        weight: String
    ): Resource<List<WishlistItem>> {
        val uid = authRepository.getSavedUid() ?: return Resource.Error("Not logged in")
        return try {
            val existing = when (val r = firestoreRepository.loadWishlist(uid)) {
                is Resource.Success -> r.data.toMutableList()
                is Resource.Error -> return Resource.Error(r.message ?: "Failed to load wishlist")
                else -> return Resource.Error("Failed to load wishlist")
            }
            if (existing.any { it.productId == productId }) {
                return Resource.Success(existing)
            }
            val item = WishlistItem(
                id = productId,
                productId = productId,
                productName = productName,
                productImage = productImage,
                productSlug = productSlug,
                price = price,
                mrp = mrp,
                weight = weight,
                addedAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).format(java.util.Date())
            )
            existing.add(item)
            when (firestoreRepository.saveWishlist(uid, existing)) {
                is Resource.Success -> Resource.Success(existing)
                is Resource.Error -> Resource.Error("Failed to save wishlist")
                else -> Resource.Error("Unknown error")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add to wishlist")
        }
    }

    suspend fun removeFromWishlist(productId: String): Resource<List<WishlistItem>> {
        val uid = authRepository.getSavedUid() ?: return Resource.Error("Not logged in")
        return try {
            val existing = when (val r = firestoreRepository.loadWishlist(uid)) {
                is Resource.Success -> r.data.toMutableList()
                is Resource.Error -> return Resource.Error(r.message ?: "Failed to load wishlist")
                else -> return Resource.Error("Failed to load wishlist")
            }
            existing.removeAll { it.productId == productId }
            when (firestoreRepository.saveWishlist(uid, existing)) {
                is Resource.Success -> Resource.Success(existing)
                is Resource.Error -> Resource.Error("Failed to save wishlist")
                else -> Resource.Error("Unknown error")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove from wishlist")
        }
    }
}
