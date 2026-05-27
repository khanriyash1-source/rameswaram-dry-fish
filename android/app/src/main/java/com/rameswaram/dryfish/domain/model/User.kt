package com.rameswaram.dryfish.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val avatar: String?,
    val addresses: List<Address>,
    val defaultAddressId: String?,
    val createdAt: String
)
