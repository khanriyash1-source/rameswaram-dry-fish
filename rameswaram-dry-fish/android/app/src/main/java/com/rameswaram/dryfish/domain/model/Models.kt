package com.rameswaram.dryfish.domain.model

// User Model
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val profileImage: String? = null,
    val addresses: List<Address> = emptyList()
)

// Address Model
data class Address(
    val id: String,
    val name: String,
    val addressLine1: String,
    val addressLine2: String = "",
    val city: String,
    val state: String,
    val pincode: String,
    val phone: String,
    val isDefault: Boolean = false
)

// Product Model
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val mrp: Double,
    val imageUrl: String? = null,
    val images: List<String> = emptyList(),
    val category: String,
    val subcategory: String = "",
    val weight: Double = 0.0,
    val weightUnit: String = "kg",
    val stockQuantity: Int = 0,
    val isAvailable: Boolean = true,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val isFavorite: Boolean = false
)

// Cart Item Model
data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null,
    val weight: Double = 0.0,
    val maxStock: Int = Int.MAX_VALUE
)

// Order Model
data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val discount: Double = 0.0,
    val shippingCharge: Double = 0.0,
    val grandTotal: Double,
    val status: OrderStatus,
    val address: Address,
    val paymentId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedDelivery: Long? = null
)

// Order Item
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    RETURNED
}

// Order Item
Ddata class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double
)

// Category
data class Category(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val productCount: Int = 0
)