package com.rameswaram.dryfish.domain.model

data class Category(
    val id: String,
    val name: String,
    val nameTamil: String?,
    val slug: String,
    val image: String?,
    val productCount: Int
)
