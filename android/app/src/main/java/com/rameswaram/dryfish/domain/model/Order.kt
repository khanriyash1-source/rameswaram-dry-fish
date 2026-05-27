package com.rameswaram.dryfish.domain.model

data class Order(
    val id: String,
    val orderNumber: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val timeline: List<OrderTimeline>,
    val shippingAddress: Address,
    val paymentMethod: PaymentMethod,
    val paymentStatus: String,
    val subtotal: Double,
    val deliveryCharge: Double,
    val discount: Double,
    val total: Double,
    val couponApplied: String?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String,
    val estimatedDelivery: String?
)

data class OrderItem(
    val productId: String,
    val productName: String,
    val productImage: String,
    val weight: String,
    val quantity: Int,
    val price: Double
)

data class OrderTimeline(
    val status: OrderStatus,
    val timestamp: String,
    val description: String
)

enum class OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURNED;

    fun displayName(): String = when (this) {
        PENDING -> "Pending"
        CONFIRMED -> "Confirmed"
        PROCESSING -> "Processing"
        SHIPPED -> "Shipped"
        DELIVERED -> "Delivered"
        CANCELLED -> "Cancelled"
        RETURNED -> "Returned"
    }

    fun isActive(): Boolean = this != CANCELLED && this != RETURNED
    fun isDelivered(): Boolean = this == DELIVERED
}

enum class PaymentMethod {
    COD, RAZORPAY;

    fun displayName(): String = when (this) {
        COD -> "Cash on Delivery"
        RAZORPAY -> "Razorpay"
    }
}
