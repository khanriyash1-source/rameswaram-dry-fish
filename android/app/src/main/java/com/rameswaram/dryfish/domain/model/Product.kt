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
)
