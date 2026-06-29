package com.rameswaram.dryfish.data.repository

import com.rameswaram.dryfish.data.local.LocalProductDataSource
import com.rameswaram.dryfish.domain.model.Category
import com.rameswaram.dryfish.domain.model.Product
import com.rameswaram.dryfish.domain.model.SKU
import com.rameswaram.dryfish.utils.Resource
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository(
    private val localDataSource: LocalProductDataSource,
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val TAG = "DryFishProductRepo"
    }

    private var firestoreProducts: List<Product>? = null

    suspend fun refreshFromFirestore() {
        Log.d(TAG, "refreshFromFirestore: started")
        firestoreProducts = try {
            val docs = firestore.collection("admin_products").get().await()
            Log.d(TAG, "refreshFromFirestore: got ${docs.size()} docs from Firestore")
            val products = docs.mapNotNull { doc ->
                val d = doc.data
                val rawSkus = d["skus"] as? List<Map<String, Any>> ?: emptyList()
                val skus = rawSkus.map { s ->
                    SKU(
                        id = s["id"] as? String ?: "",
                        weight = s["weight"] as? String ?: "",
                        price = (s["price"] as? Number)?.toDouble() ?: 0.0,
                        stock = (s["stock"] as? Number)?.toInt() ?: 0,
                        weightInGrams = (s["weightInGrams"] as? Number)?.toInt() ?: 0,
                        isAvailable = s["isAvailable"] as? Boolean ?: false,
                        mrp = (s["mrp"] as? Number)?.toDouble() ?: 0.0
                    )
                }
                Product(
                    id = d["id"] as? String ?: doc.id,
                    name = d["name"] as? String ?: "",
                    nameTamil = d["nameTamil"] as? String,
                    slug = d["slug"] as? String ?: "",
                    description = d["description"] as? String,
                    shortDesc = d["shortDesc"] as? String,
                    category = d["category"] as? String ?: "",
                    images = (d["images"] as? List<String>) ?: emptyList(),
                    skus = skus,
                    tags = (d["tags"] as? List<String>) ?: emptyList(),
                    isFeatured = d["isFeatured"] as? Boolean ?: false,
                    isBestseller = d["isBestseller"] as? Boolean ?: false,
                    rating = (d["rating"] as? Number)?.toDouble() ?: 0.0,
                    reviewCount = (d["reviewCount"] as? Number)?.toInt() ?: 0,
                    isEnabled = (d["isEnabled"] as? Boolean) ?: (d["enabled"] as? Boolean) ?: true
                )
            }
            Log.d(TAG, "refreshFromFirestore: parsed ${products.size} products")
            products
        } catch (e: Exception) {
            Log.e(TAG, "refreshFromFirestore: FAILED: ${e.message}", e)
            firestoreProducts
        }
    }

    private fun mergedProducts(): List<Product> {
        val local = localDataSource.getProducts()
        Log.d(TAG, "mergedProducts: local=${local.size}, firestore=${firestoreProducts?.size ?: 0}")
        val map = local.associateBy { it.id }.toMutableMap()
        firestoreProducts?.forEach { map[it.id] = it }
        val result = map.values.toList().filter { it.isEnabled }
        Log.d(TAG, "mergedProducts: total=${result.size} (${result.size} enabled)")
        return result
    }

    fun getProducts(
        page: Int = 1,
        category: String? = null,
        search: String? = null,
        sort: String? = null
    ): Resource<List<Product>> {
        var products = mergedProducts()
        if (category != null) {
            products = products.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (!search.isNullOrBlank()) {
            val q = search.lowercase()
            products = products.filter {
                it.name.lowercase().contains(q) ||
                it.nameTamil?.lowercase()?.contains(q) == true ||
                it.shortDesc?.lowercase()?.contains(q) == true
            }
        }
        if (sort != null) {
            products = when (sort) {
                "price_asc" -> products.sortedBy { it.price }
                "price_desc" -> products.sortedByDescending { it.price }
                "name" -> products.sortedBy { it.name }
                "rating" -> products.sortedByDescending { it.rating }
                else -> products
            }
        }
        return Resource.Success(products)
    }

    fun getFeaturedProducts(): Resource<List<Product>> {
        val all = mergedProducts()
        return Resource.Success(all.filter { it.isFeatured })
    }

    fun getBestsellers(): Resource<List<Product>> {
        val all = mergedProducts()
        return Resource.Success(all.filter { it.isBestseller || it.isFeatured })
    }

    fun getProductBySlug(slug: String): Resource<Product> {
        val product = mergedProducts().find { it.slug == slug }
        return if (product != null) Resource.Success(product)
        else Resource.Error("Product not found")
    }

    fun searchProducts(query: String): Resource<List<Product>> {
        return getProducts(search = query)
    }

    fun getCategories(): Resource<List<Category>> {
        return Resource.Success(localDataSource.getCategories())
    }
}
