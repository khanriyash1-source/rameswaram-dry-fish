package com.rameswaram.dryfish.domain.model

data class Product(
    val id: String,
    val name: String,
    val nameTamil: String?,
    val slug: String,
    val description: String?,
    val shortDesc: String?,
    val category: String,
    val images: List<String>,
    val skus: List<SKU>,
    val tags: List<String>,
    val isFeatured: Boolean,
    val isBestseller: Boolean,
    val rating: Double,
    val reviewCount: Int
) {
    // Convenience properties for bilingual display
    val nameEn: String get() = name
    val nameTa: String get() = nameTamil ?: name
    
    // Get first available price for display
    val price: Double get() = skus.firstOrNull()?.price ?: 0.0
}
