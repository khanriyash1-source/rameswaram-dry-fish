package com.rameswaram.dryfish.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.rameswaram.dryfish.domain.model.Address
import com.rameswaram.dryfish.domain.model.CartItem
import com.rameswaram.dryfish.domain.model.Order
import com.rameswaram.dryfish.domain.model.User
import com.rameswaram.dryfish.domain.model.WishlistItem
import com.rameswaram.dryfish.utils.Resource
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val TAG = "DryFishFirestore"
    }

    private fun userRef(uid: String) = firestore.collection("users").document(uid)

    suspend fun saveUser(user: User): Resource<User> {
        return try {
            Log.d(TAG, "saveUser: uid=${user.id}, name=${user.name}")
            val updates = hashMapOf<String, Any>(
                "id" to user.id,
                "name" to user.name,
                "email" to user.email
            )
            user.phone?.let { updates["phone"] = it }
            user.avatar?.let { updates["avatar"] = it }
            userRef(user.id).set(updates, SetOptions.merge()).await()
            Log.d(TAG, "saveUser: SUCCESS (merged, preserved totalSpent/orderCount)")
            Resource.Success(user)
        } catch (e: Exception) {
            Log.e(TAG, "saveUser: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to save user")
        }
    }

    suspend fun getUser(uid: String): Resource<User?> {
        return try {
            Log.d(TAG, "getUser: uid=$uid")
            val doc = userRef(uid).get().await()
            Log.d(TAG, "getUser: exists=${doc.exists()}")
            if (doc.exists()) {
                val data = doc.data
                val user = User(
                    id = data?.get("id") as? String ?: uid,
                    name = data?.get("name") as? String ?: "",
                    email = data?.get("email") as? String ?: "",
                    phone = data?.get("phone") as? String,
                    avatar = data?.get("avatar") as? String,
                    totalSpent = data?.get("totalSpent") as? Double ?: 0.0,
                    orderCount = (data?.get("orderCount") as? Long)?.toInt() ?: 0,
                    isTamilLanguage = data?.get("isTamilLanguage") as? Boolean ?: false,
                    defaultAddressId = data?.get("defaultAddressId") as? String,
                    createdAt = data?.get("createdAt") as? String ?: ""
                )
                Log.d(TAG, "getUser: totalSpent=${user.totalSpent}, orderCount=${user.orderCount}")
                Resource.Success(user)
            } else {
                Resource.Success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "getUser: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to load user")
        }
    }

    suspend fun updateProfile(uid: String, name: String?, phone: String?, avatar: String?): Resource<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>()
            name?.let { updates["name"] = it }
            phone?.let { updates["phone"] = it }
            avatar?.let { updates["avatar"] = it }
            if (updates.isNotEmpty()) {
                Log.d(TAG, "updateProfile: uid=$uid updates=$updates")
                userRef(uid).update(updates).await()
                Log.d(TAG, "updateProfile: SUCCESS")
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "updateProfile: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to update profile")
        }
    }

    suspend fun updateLanguage(uid: String, isTamil: Boolean): Resource<Unit> {
        return try {
            Log.d(TAG, "updateLanguage: uid=$uid isTamil=$isTamil")
            userRef(uid).update("isTamilLanguage", isTamil).await()
            Log.d(TAG, "updateLanguage: SUCCESS")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "updateLanguage: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to update language")
        }
    }

    suspend fun saveCart(uid: String, items: List<CartItem>): Resource<Unit> {
        return try {
            Log.d(TAG, "saveCart: uid=$uid, itemCount=${items.size}")
            val batch = firestore.batch()
            val cartRef = userRef(uid).collection("cart")
            val existing = cartRef.get().await()
            existing.forEach { batch.delete(it.reference) }
            items.forEach { item ->
                batch.set(cartRef.document(item.id), item)
            }
            batch.commit().await()
            Log.d(TAG, "saveCart: SUCCESS (saved ${items.size} items)")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "saveCart: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to save cart")
        }
    }

    suspend fun loadCart(uid: String): Resource<List<CartItem>> {
        return try {
            Log.d(TAG, "loadCart: uid=$uid")
            val docs = userRef(uid).collection("cart").get().await()
            val items = docs.mapNotNull { it.toObject(CartItem::class.java) }
            Log.d(TAG, "loadCart: items=${items.size} (raw docs=${docs.size()})")
            Resource.Success(items)
        } catch (e: Exception) {
            Log.e(TAG, "loadCart: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to load cart")
        }
    }

    suspend fun saveOrder(uid: String, order: Order): Resource<Unit> {
        return try {
            Log.d(TAG, "saveOrder: uid=$uid, orderId=${order.id}")
            userRef(uid).collection("orders").document(order.id).set(order).await()
            Log.d(TAG, "saveOrder: SUCCESS")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "saveOrder: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to save order")
        }
    }

    suspend fun loadOrders(uid: String): Resource<List<Order>> {
        return try {
            Log.d(TAG, "loadOrders: uid=$uid")
            val docs = userRef(uid).collection("orders")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()
            val orders = docs.mapNotNull { it.toObject(Order::class.java) }
            Log.d(TAG, "loadOrders: orders=${orders.size}")
            Resource.Success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "loadOrders: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to load orders")
        }
    }

    suspend fun getOrder(uid: String, orderId: String): Resource<Order?> {
        return try {
            Log.d(TAG, "getOrder: uid=$uid, orderId=$orderId")
            val doc = userRef(uid).collection("orders").document(orderId).get().await()
            Log.d(TAG, "getOrder: exists=${doc.exists()}")
            Resource.Success(doc.toObject(Order::class.java))
        } catch (e: Exception) {
            Log.e(TAG, "getOrder: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to load order")
        }
    }

    suspend fun saveAddresses(uid: String, addresses: List<Address>): Resource<Unit> {
        return try {
            Log.d(TAG, "saveAddresses: uid=$uid, count=${addresses.size}")
            val batch = firestore.batch()
            val addrRef = userRef(uid).collection("addresses")
            val existing = addrRef.get().await()
            existing.forEach { batch.delete(it.reference) }
            addresses.forEach { addr ->
                batch.set(addrRef.document(addr.id), addr)
            }
            batch.commit().await()
            Log.d(TAG, "saveAddresses: SUCCESS")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "saveAddresses: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to save addresses")
        }
    }

    suspend fun loadAddresses(uid: String): Resource<List<Address>> {
        return try {
            Log.d(TAG, "loadAddresses: uid=$uid")
            val docs = userRef(uid).collection("addresses").get().await()
            val addresses = docs.map { it.toObject(Address::class.java) }
            Log.d(TAG, "loadAddresses: addresses=${addresses.size}")
            Resource.Success(addresses)
        } catch (e: Exception) {
            Log.e(TAG, "loadAddresses: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to load addresses")
        }
    }

    suspend fun saveWishlist(uid: String, items: List<WishlistItem>): Resource<Unit> {
        return try {
            Log.d(TAG, "saveWishlist: uid=$uid, itemCount=${items.size}")
            val batch = firestore.batch()
            val wishlistRef = userRef(uid).collection("wishlist")
            val existing = wishlistRef.get().await()
            existing.forEach { batch.delete(it.reference) }
            items.forEach { item ->
                batch.set(wishlistRef.document(item.productId), item)
            }
            batch.commit().await()
            Log.d(TAG, "saveWishlist: SUCCESS")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "saveWishlist: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to save wishlist")
        }
    }

    suspend fun loadWishlist(uid: String): Resource<List<WishlistItem>> {
        return try {
            Log.d(TAG, "loadWishlist: uid=$uid")
            val docs = userRef(uid).collection("wishlist").get().await()
            val items = docs.mapNotNull { it.toObject(WishlistItem::class.java) }
            Log.d(TAG, "loadWishlist: items=${items.size}")
            Resource.Success(items)
        } catch (e: Exception) {
            Log.e(TAG, "loadWishlist: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to load wishlist")
        }
    }

    suspend fun getOrderSpendingSummary(uid: String): Resource<Pair<Int, Double>> {
        return try {
            Log.d(TAG, "getOrderSpendingSummary: uid=$uid")
            val docs = userRef(uid).collection("orders").get().await()
            var count = 0
            var total = 0.0
            for (doc in docs) {
                total += (doc.data["total"] as? Number)?.toDouble() ?: 0.0
                count++
            }
            Log.d(TAG, "getOrderSpendingSummary: orders=$count, total=$total")
            Resource.Success(Pair(count, total))
        } catch (e: Exception) {
            Log.e(TAG, "getOrderSpendingSummary: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to load order summary")
        }
    }

    suspend fun updateSpending(uid: String, amount: Double): Resource<Unit> {
        return try {
            Log.d(TAG, "updateSpending: uid=$uid, amount=$amount")
            firestore.runTransaction { transaction ->
                val ref = userRef(uid)
                val snapshot = transaction.get(ref)
                val currentSpent = snapshot.getDouble("totalSpent") ?: 0.0
                val currentCount = snapshot.getLong("orderCount")?.toInt() ?: 0
                transaction.update(ref, "totalSpent", currentSpent + amount)
                transaction.update(ref, "orderCount", currentCount + 1)
            }.await()
            Log.d(TAG, "updateSpending: SUCCESS")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "updateSpending: FAILED: ${e.message}", e)
            Resource.Error(e.message ?: "Failed to update spending")
        }
    }
}
