package com.rameswaram.dryfish.domain.model

data class WishlistItem(
    val id: String,
    val productId: String,
    val productName: String,
    val productImage: String,
    val productSlug: String,
    val price: Double,
    val mrp: Double,
    val weight: String,
    val addedAt: String
) {
    constructor() : this("", "", "", "", "", 0.0, 0.0, "", "")
}
