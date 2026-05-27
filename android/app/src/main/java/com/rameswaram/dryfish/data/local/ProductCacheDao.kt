package com.rameswaram.dryfish.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductCacheDao {

    @Query("SELECT * FROM product_cache WHERE category = :category")
    fun getProductsByCategory(category: String): Flow<List<ProductCacheEntity>>

    @Query("SELECT * FROM product_cache WHERE is_featured = 1")
    fun getFeaturedProducts(): Flow<List<ProductCacheEntity>>

    @Query("SELECT * FROM product_cache WHERE is_bestseller = 1")
    fun getBestsellers(): Flow<List<ProductCacheEntity>>

    @Query("SELECT * FROM product_cache WHERE id = :id")
    suspend fun getProductById(id: String): ProductCacheEntity?

    @Query("SELECT * FROM product_cache WHERE slug = :slug")
    suspend fun getProductBySlug(slug: String): ProductCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductCacheEntity>)

    @Query("DELETE FROM product_cache")
    suspend fun clearCache()

    @Query("SELECT COUNT(*) FROM product_cache")
    suspend fun getCacheSize(): Int

    @Query("DELETE FROM product_cache WHERE cached_at < :expiryTime")
    suspend fun clearExpiredCache(expiryTime: Long)
}
