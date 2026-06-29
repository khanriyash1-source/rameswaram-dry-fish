package com.rameswaram.dryfish.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rameswaram.dryfish.domain.model.Order
import com.rameswaram.dryfish.domain.model.OrderStatus
import com.rameswaram.dryfish.domain.model.Product
import com.rameswaram.dryfish.domain.model.SKU
import com.rameswaram.dryfish.domain.model.User
import com.rameswaram.dryfish.utils.Constants
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.tasks.await

class AdminRepository(
    private val firestore: FirebaseFirestore,
    private val context: Context
) {
    companion object {
        private const val TAG = "DryFishAdmin"
    }

    suspend fun getProducts(): Resource<List<Product>> {
        return try {
            Log.d(TAG, "getProducts")

            // 1. Load local products from JSON
            val localJson = context.assets.open("products.json").bufferedReader().use { it.readText() }
            val localType = object : TypeToken<Map<String, List<Product>>>() {}.type
            val localResponse: Map<String, List<Product>> = Gson().fromJson(localJson, localType)
            val localProducts = (localResponse["products"] ?: emptyList()).map { p ->
                p.copy(images = p.images.map { "file:///android_asset/images/$it" })
            }

            // 2. Load Firestore products
            val docs = firestore.collection("admin_products").get().await()
            val firestoreProducts = docs.mapNotNull { doc ->
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

            // 3. Merge: Firestore overrides local by product ID
            val merged = localProducts.associateBy { it.id }.toMutableMap()
            firestoreProducts.forEach { merged[it.id] = it }
            val allProducts = merged.values.toList()

            Log.d(TAG, "getProducts: local=${localProducts.size}, firestore=${firestoreProducts.size}, merged=${allProducts.size}")
            Resource.Success(allProducts)
        } catch (e: Exception) {
            Log.e(TAG, "getProducts: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to load products")
        }
    }

    suspend fun saveProduct(product: Product): Resource<Unit> {
        return try {
            Log.d(TAG, "saveProduct: id=${product.id}, name=${product.name}, skus=${product.skus.size}")
            product.skus.forEachIndexed { i, s ->
                Log.d(TAG, "  sku[$i]: id=${s.id}, weight=${s.weight}, price=${s.price}, stock=${s.stock}, mrp=${s.mrp}, grams=${s.weightInGrams}, avail=${s.isAvailable}")
            }
            firestore.collection("admin_products").document(product.id).set(product).await()
            Log.d(TAG, "saveProduct: SUCCESS for ${product.id}")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "saveProduct: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to save product")
        }
    }

    suspend fun deleteProduct(productId: String): Resource<Unit> {
        return try {
            Log.d(TAG, "deleteProduct: id=$productId")
            firestore.collection("admin_products").document(productId).delete().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "deleteProduct: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to delete product")
        }
    }

    suspend fun getAllOrders(): Resource<List<Order>> {
        return try {
            Log.d(TAG, "getAllOrders")
            val docs = firestore.collection("all_orders")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()
            val orders = docs.mapNotNull { it.toObject(Order::class.java) }
            Resource.Success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "getAllOrders: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to load orders")
        }
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Resource<Unit> {
        return try {
            Log.d(TAG, "updateOrderStatus: orderId=$orderId, status=$status")
            firestore.collection("all_orders").document(orderId)
                .update("status", status.name).await()
            val orderDoc = firestore.collection("all_orders").document(orderId).get().await()
            val userId = orderDoc.getString("userId")
            if (!userId.isNullOrEmpty()) {
                firestore.collection("users").document(userId)
                    .collection("orders").document(orderId)
                    .update("status", status.name).await()
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "updateOrderStatus: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to update order status")
        }
    }

    suspend fun getAllUsers(): Resource<List<User>> {
        return try {
            Log.d(TAG, "getAllUsers")
            val docs = firestore.collection("users").get().await()
            val users = docs.mapNotNull { doc ->
                val data = doc.data
                User(
                    id = data["id"] as? String ?: doc.id,
                    name = data["name"] as? String ?: "",
                    email = data["email"] as? String ?: "",
                    phone = data["phone"] as? String,
                    avatar = data["avatar"] as? String,
                    totalSpent = data["totalSpent"] as? Double ?: 0.0,
                    orderCount = (data["orderCount"] as? Long)?.toInt() ?: 0,
                    isTamilLanguage = data["isTamilLanguage"] as? Boolean ?: false,
                    createdAt = data["createdAt"] as? String ?: ""
                )
            }
            Resource.Success(users)
        } catch (e: Exception) {
            Log.e(TAG, "getAllUsers: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to load users")
        }
    }

    suspend fun getDashboardStats(): Resource<DashboardStats> {
        return try {
            val productsCount = firestore.collection("admin_products").get().await().size()
            val ordersCount = firestore.collection("all_orders").get().await().size()
            val usersCount = firestore.collection("users").get().await().size()
            var totalRevenue = 0.0
            val orderDocs = firestore.collection("all_orders").get().await()
            for (doc in orderDocs) {
                totalRevenue += (doc.data["total"] as? Number)?.toDouble() ?: 0.0
            }
            Resource.Success(DashboardStats(productsCount, ordersCount, usersCount, totalRevenue))
        } catch (e: Exception) {
            Log.e(TAG, "getDashboardStats: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to load stats")
        }
    }

    suspend fun resetAllData(): Resource<String> {
        return try {
            Log.d(TAG, "resetAllData: started")

            // 1. Clear all_orders
            val allOrdersDocs = firestore.collection("all_orders").get().await()
            val batch1 = firestore.batch()
            for (doc in allOrdersDocs.documents) {
                batch1.delete(doc.reference)
            }
            batch1.commit().await()
            Log.d(TAG, "resetAllData: cleared ${allOrdersDocs.size()} orders from all_orders")

            // 2. For each user, clear orders, cart, reset spending
            val userDocs = firestore.collection("users").get().await()
            var resetCount = 0
            for (userDoc in userDocs.documents) {
                val uid = userDoc.id
                val batch2 = firestore.batch()

                // Clear user's orders subcollection
                val userOrders = firestore.collection("users").document(uid)
                    .collection("orders").get().await()
                for (orderDoc in userOrders.documents) {
                    batch2.delete(orderDoc.reference)
                }

                // Clear user's cart subcollection
                val cartItems = firestore.collection("users").document(uid)
                    .collection("cart").get().await()
                for (cartDoc in cartItems.documents) {
                    batch2.delete(cartDoc.reference)
                }

                batch2.commit().await()

                // Reset spending stats
                firestore.collection("users").document(uid)
                    .update("totalSpent", 0.0, "orderCount", 0).await()
                resetCount++
            }
            Log.d(TAG, "resetAllData: reset $resetCount users")

            Resource.Success("Cleared ${allOrdersDocs.size()} orders, reset $resetCount users")
        } catch (e: Exception) {
            Log.e(TAG, "resetAllData: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to reset data")
        }
    }

    suspend fun syncProductsFromAssets(): Resource<Int> {
        return try {
            Log.d(TAG, "syncProductsFromAssets: started")
            val json = context.assets.open("products.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<Product>>>() {}.type
            val response: Map<String, List<Product>> = Gson().fromJson(json, type)
            val products = response["products"] ?: emptyList()
            val imageBase = Constants.BASE_URL.replace("/api/v1/", "") + "/images/"
            var count = 0
            for (product in products) {
                val existing = firestore.collection("admin_products").document(product.id).get().await()
                if (!existing.exists()) {
                    val updated = product.copy(
                        images = product.images.map { "$imageBase$it" }
                    )
                    firestore.collection("admin_products").document(updated.id).set(updated).await()
                    count++
                }
            }
            Log.d(TAG, "syncProductsFromAssets: added $count new products (skipped ${products.size - count} existing)")
            Resource.Success(count)
        } catch (e: Exception) {
            Log.e(TAG, "syncProductsFromAssets: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to sync products")
        }
    }
}

data class DashboardStats(
    val productsCount: Int = 0,
    val ordersCount: Int = 0,
    val usersCount: Int = 0,
    val totalRevenue: Double = 0.0
)
