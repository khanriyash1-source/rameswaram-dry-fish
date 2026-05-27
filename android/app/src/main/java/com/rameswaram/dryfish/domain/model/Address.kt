package com.rameswaram.dryfish.domain.model

data class Address(
    val id: String,
    val name: String,
    val phone: String,
    val street: String,
    val city: String,
    val state: String,
    val pincode: String,
    val isDefault: Boolean = false,
    val label: String? = null
) {
    val fullAddress: String
        get() = "$street, $city, $state - $pincode"

    val formattedForDisplay: String
        get() = buildString {
            append(name)
            append("\n")
            append(phone)
            append("\n")
            append(street)
            append(", ")
            append(city)
            append(", ")
            append(state)
            append(" - ")
            append(pincode)
        }
}
