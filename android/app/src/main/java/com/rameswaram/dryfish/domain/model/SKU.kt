package com.rameswaram.dryfish.domain.model

data class SKU(
    val id: String,
    val weight: String,
    val weightInGrams: Int,
    val price: Double,
    val mrp: Double,
    val stock: Int,
    val isAvailable: Boolean = true
) {
    val discountPercent: Int
        get() = if (mrp > 0) ((mrp - price) / mrp * 100).toInt() else 0
}
