package com.rameswaram.dryfish.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items")
    fun getAllItems(): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart_items WHERE id = :id")
    suspend fun getItemById(id: String): CartEntity?

    @Query("SELECT COUNT(*) FROM cart_items")
    fun getItemCount(): Flow<Int>

    @Query("SELECT SUM(price * quantity) FROM cart_items")
    fun getCartTotal(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<CartEntity>)

    @Update
    suspend fun updateItem(item: CartEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: String, quantity: Int)

    @Delete
    suspend fun deleteItem(item: CartEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteItemById(id: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT EXISTS(SELECT 1 FROM cart_items WHERE id = :id)")
    suspend fun itemExists(id: String): Boolean
}
