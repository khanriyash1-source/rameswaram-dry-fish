package com.rameswaram.dryfish.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items WHERE user_id = :userId")
    fun getAllItems(userId: String): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart_items WHERE user_id = :userId")
    suspend fun getAllItemsOnce(userId: String): List<CartEntity>

    @Query("SELECT * FROM cart_items WHERE user_id = :userId AND id = :id")
    suspend fun getItemById(userId: String, id: String): CartEntity?

    @Query("SELECT COUNT(*) FROM cart_items WHERE user_id = :userId")
    fun getItemCount(userId: String): Flow<Int>

    @Query("SELECT SUM(price * quantity) FROM cart_items WHERE user_id = :userId")
    fun getCartTotal(userId: String): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<CartEntity>)

    @Update
    suspend fun updateItem(item: CartEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE user_id = :userId AND id = :id")
    suspend fun updateQuantity(userId: String, id: String, quantity: Int)

    @Delete
    suspend fun deleteItem(item: CartEntity)

    @Query("DELETE FROM cart_items WHERE user_id = :userId AND id = :id")
    suspend fun deleteItemById(userId: String, id: String)

    @Query("DELETE FROM cart_items WHERE user_id = :userId")
    suspend fun clearCart(userId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM cart_items WHERE user_id = :userId AND id = :id)")
    suspend fun itemExists(userId: String, id: String): Boolean
}
