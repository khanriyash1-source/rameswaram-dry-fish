package com.rameswaram.dryfish.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String? = null,
    val avatar: String? = null,
    val addresses: List<Address> = emptyList(),
    val defaultAddressId: String? = null,
    val isTamilLanguage: Boolean = false,
    val totalSpent: Double = 0.0,
    val orderCount: Int = 0,
    val createdAt: String = ""
) {
    constructor() : this("", "", "")
}
