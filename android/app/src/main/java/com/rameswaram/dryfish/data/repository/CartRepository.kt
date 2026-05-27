package com.rameswaram.dryfish.data.repository

import com.rameswaram.dryfish.data.api.ApiService
import com.rameswaram.dryfish.data.local.CartDao
import com.rameswaram.dryfish.data.local.CartEntity
import com.rameswaram.dryfish.domain.model.CartItem
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepository(
    private val apiService: ApiService,
    private val cartDao: CartDao
) {
    fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllItems().map { entities ->
            entities.map { it.toCartItem() }
        }
    }

    fun getCartTotal(): Flow<Double> {
        return cartDao.getCartTotal().map { it ?: 0.0 }
    }

    fun getCartItemCount(): Flow<Int> {
        return cartDao.getItemCount()
    }

    suspend fun addToCart(item: CartItem): Resource<CartItem> {
        return try {
            cartDao.insertItem(CartEntity.fromCartItem(item))
            Resource.Success(item)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add to cart")
        }
    }

    suspend fun updateQuantity(id: String, quantity: Int): Resource<CartItem> {
        return try {
            cartDao.updateQuantity(id, quantity)
            val entity = cartDao.getItemById(id)
            if (entity != null) Resource.Success(entity.toCartItem())
            else Resource.Error("Item not found")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update quantity")
        }
    }

    suspend fun removeFromCart(id: String): Resource<Unit> {
        return try {
            cartDao.deleteItemById(id)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove from cart")
        }
    }

    suspend fun clearCart(): Resource<Unit> {
        return try {
            cartDao.clearCart()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to clear cart")
        }
    }

    suspend fun syncWithServer(): Resource<List<CartItem>> {
        return try {
            val response = apiService.getCart()
            if (response.isSuccessful && response.body()?.success == true) {
                val serverItems = response.body()!!.data ?: emptyList()
                cartDao.clearCart()
                cartDao.insertItems(serverItems.map { CartEntity.fromCartItem(it) })
                Resource.Success(serverItems)
            } else {
                Resource.Error(response.body()?.message ?: "Sync failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sync failed")
        }
    }
}
