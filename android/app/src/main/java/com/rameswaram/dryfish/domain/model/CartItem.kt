package com.rameswaram.dryfish.domain.model

data class CartItem(
    val id: String,
    val productId: String,
    val productName: String,
    val productImage: String,
    val selectedSkuId: String,
    val weight: String,
    val price: Double,
    val mrp: Double,
    val quantity: Int,
    val maxQuantity: Int = 10,
    val productSlug: String = ""
) {
    constructor() : this("", "", "", "", "", "", 0.0, 0.0, 0)

    val subtotal: Double get() = price * quantity
    val totalMrp: Double get() = mrp * quantity
    val savings: Double get() = totalMrp - subtotal
}
