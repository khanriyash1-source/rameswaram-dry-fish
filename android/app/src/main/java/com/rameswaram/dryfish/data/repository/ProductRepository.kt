package com.rameswaram.dryfish.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rameswaram.dryfish.data.api.ApiService
import com.rameswaram.dryfish.data.local.ProductCacheDao
import com.rameswaram.dryfish.data.local.ProductCacheEntity
import com.rameswaram.dryfish.domain.model.Category
import com.rameswaram.dryfish.domain.model.Product
import com.rameswaram.dryfish.domain.model.SKU
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ProductRepository(
    private val apiService: ApiService,
    private val productCacheDao: ProductCacheDao
) {
    private val gson = Gson()
    private val cacheDuration = 30 * 60 * 1000L

    suspend fun getProducts(
        page: Int = 1,
        category: String? = null,
        search: String? = null,
        sort: String? = null
    ): Resource<List<Product>> {
        return try {
            val response = apiService.getProducts(page, 20, category, search, sort)
            if (response.isSuccessful && response.body()?.success == true) {
                val products = response.body()!!.data ?: emptyList()
                cacheProducts(products)
                Resource.Success(products)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load products")
            }
        } catch (e: Exception) {
            val cached = getCachedProducts(category)
            if (cached.isNotEmpty()) Resource.Success(cached)
            else Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun getFeaturedProducts(): Resource<List<Product>> {
        return try {
            val response = apiService.getFeaturedProducts()
            if (response.isSuccessful && response.body()?.success == true) {
                val products = response.body()!!.data ?: emptyList()
                cacheProducts(products)
                Resource.Success(products)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load featured")
            }
        } catch (e: Exception) {
            val cached = getCachedFeatured()
            if (cached.isNotEmpty()) Resource.Success(cached)
            else Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun getBestsellers(): Resource<List<Product>> {
        return try {
            val response = apiService.getBestsellers()
            if (response.isSuccessful && response.body()?.success == true) {
                val products = response.body()!!.data ?: emptyList()
                cacheProducts(products)
                Resource.Success(products)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load bestsellers")
            }
        } catch (e: Exception) {
            val cached = getCachedBestsellers()
            if (cached.isNotEmpty()) Resource.Success(cached)
            else Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun getProductBySlug(slug: String): Resource<Product> {
        return try {
            val response = apiService.getProductBySlug(slug)
            if (response.isSuccessful && response.body()?.success == true) {
                val product = response.body()!!.data!!
                Resource.Success(product)
            } else {
                val cached = productCacheDao.getProductBySlug(slug)
                if (cached != null) Resource.Success(cached.toProduct())
                else Resource.Error(response.body()?.message ?: "Product not found")
            }
        } catch (e: Exception) {
            val cached = productCacheDao.getProductBySlug(slug)
            if (cached != null) Resource.Success(cached.toProduct())
            else Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun searchProducts(query: String): Resource<List<Product>> {
        return getProducts(search = query)
    }

    suspend fun getCategories(): Resource<List<Category>> {
        return try {
            val response = apiService.getCategories()
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data ?: emptyList())
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load categories")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    private suspend fun cacheProducts(products: List<Product>) {
        val entities = products.map { it.toCacheEntity() }
        productCacheDao.insertProducts(entities)
    }

    private suspend fun getCachedProducts(category: String?): List<Product> {
        return if (category != null) {
            productCacheDao.getProductsByCategory(category).first().map { it.toProduct() }
        } else {
            emptyList()
        }
    }

    private suspend fun getCachedFeatured(): List<Product> {
        return productCacheDao.getFeaturedProducts().first().map { it.toProduct() }
    }

    private suspend fun getCachedBestsellers(): List<Product> {
        return productCacheDao.getBestsellers().first().map { it.toProduct() }
    }

    fun getCachedProductsFlow(): Flow<List<Product>> {
        return productCacheDao.getFeaturedProducts().map { entities ->
            entities.map { it.toProduct() }
        }
    }

    private fun Product.toCacheEntity(): ProductCacheEntity {
        return ProductCacheEntity(
            id = id,
            slug = slug,
            name = name,
            nameTamil = nameTamil,
            description = description,
            shortDesc = shortDesc,
            category = category,
            imagesJson = gson.toJson(images),
            skusJson = gson.toJson(skus),
            tagsJson = gson.toJson(tags),
            isFeatured = isFeatured,
            isBestseller = isBestseller,
            rating = rating,
            reviewCount = reviewCount
        )
    }

    private fun ProductCacheEntity.toProduct(): Product {
        val imageType = object : TypeToken<List<String>>() {}.type
        val skuType = object : TypeToken<List<SKU>>() {}.type
        val tagType = object : TypeToken<List<String>>() {}.type

        return Product(
            id = id,
            slug = slug,
            name = name,
            nameTamil = nameTamil,
            description = description,
            shortDesc = shortDesc,
            category = category,
            images = gson.fromJson(imagesJson, imageType),
            skus = gson.fromJson(skusJson, skuType),
            tags = gson.fromJson(tagsJson, tagType),
            isFeatured = isFeatured,
            isBestseller = isBestseller,
            rating = rating,
            reviewCount = reviewCount
        )
    }
}
