package com.rameswaram.dryfish.data.repository

import android.util.Log
import com.rameswaram.dryfish.data.local.CartDao
import com.rameswaram.dryfish.data.local.CartEntity
import com.rameswaram.dryfish.domain.model.CartItem
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class CartRepository(
    private val cartDao: CartDao,
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) {
    private fun currentUserId(): String? = authRepository.getSavedUid()

    fun getCartItems(): Flow<List<CartItem>> {
        val uid = currentUserId() ?: return emptyFlow()
        return cartDao.getAllItems(uid).map { entities ->
            entities.map { it.toCartItem() }
        }
    }

    fun getCartTotal(): Flow<Double> {
        val uid = currentUserId() ?: return flowOf(0.0)
        return cartDao.getCartTotal(uid).map { it ?: 0.0 }
    }

    fun getCartItemCount(): Flow<Int> {
        val uid = currentUserId() ?: return flowOf(0)
        return cartDao.getItemCount(uid)
    }

    suspend fun loadFromFirestore(): Resource<List<CartItem>> {
        val uid = currentUserId()
        if (uid == null) return Resource.Error("No user")
        Log.d("DryFishCart", "loadFromFirestore: uid=$uid")
        return try {
            when (val result = firestoreRepository.loadCart(uid)) {
                is Resource.Success -> {
                    val items = result.data
                    Log.d("DryFishCart", "loadFromFirestore: got ${items.size} items from Firestore")
                    if (items.isNotEmpty()) {
                        cartDao.clearCart(uid)
                        items.forEach {
                            cartDao.insertItem(CartEntity.fromCartItem(it, uid))
                        }
                        Resource.Success(items)
                    } else {
                        val localItems = cartDao.getAllItemsOnce(uid)
                        Log.d("DryFishCart", "loadFromFirestore: Firestore empty, falling back to local (${localItems.size} items)")
                        Resource.Success(localItems.map { it.toCartItem() })
                    }
                }
                else -> {
                    Log.e("DryFishCart", "loadFromFirestore: Firestore load returned error")
                    Resource.Error("Firestore load failed")
                }
            }
        } catch (e: Exception) {
            Log.e("DryFishCart", "loadFromFirestore: exception: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to load cart from cloud")
        }
    }

    private suspend fun syncToFirestore() {
        val uid = currentUserId() ?: return
        val items = cartDao.getAllItemsOnce(uid).map { it.toCartItem() }
        when (val result = firestoreRepository.saveCart(uid, items)) {
            is Resource.Success -> Log.d("DryFishCart", "syncToFirestore: SUCCESS (${items.size} items)")
            is Resource.Error -> Log.e("DryFishCart", "syncToFirestore: FAILED: ${result.message}")
            else -> {}
        }
    }

    suspend fun addToCart(item: CartItem): Resource<CartItem> {
        val uid = currentUserId() ?: return Resource.Error("Not logged in")
        return try {
            cartDao.insertItem(CartEntity.fromCartItem(item, uid))
            syncToFirestore()
            Resource.Success(item)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add to cart")
        }
    }

    suspend fun updateQuantity(id: String, quantity: Int): Resource<CartItem> {
        val uid = currentUserId() ?: return Resource.Error("Not logged in")
        return try {
            cartDao.updateQuantity(uid, id, quantity)
            val entity = cartDao.getItemById(uid, id)
            if (entity != null) {
                syncToFirestore()
                Resource.Success(entity.toCartItem())
            } else Resource.Error("Item not found")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update quantity")
        }
    }

    suspend fun removeFromCart(id: String): Resource<Unit> {
        val uid = currentUserId() ?: return Resource.Error("Not logged in")
        return try {
            cartDao.deleteItemById(uid, id)
            syncToFirestore()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove from cart")
        }
    }

    suspend fun clearCart(): Resource<Unit> {
        val uid = currentUserId() ?: return Resource.Error("Not logged in")
        return try {
            cartDao.clearCart(uid)
            syncToFirestore()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to clear cart")
        }
    }
}
