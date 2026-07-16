package com.rameswaram.dryfish.utils

object Constants {
    const val BASE_URL = "https://api.rameswaramdryfish.com/api/v1/"
    const val WEB_CLIENT_ID = "646532651867-5se97kjtrqj2r3kfc38n7nfgoihp7lkk.apps.googleusercontent.com"

    const val PREFS_NAME = "rameswaram_dry_fish_prefs"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_PHONE = "user_phone"
    const val KEY_USER_AVATAR = "user_avatar"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_LANGUAGE = "language"

    const val CACHE_EXPIRY = 30 * 60 * 1000L

    val CATEGORIES = listOf(
        "All" to "அனைத்தும்",
        "Fish" to "மீன்",
        "Prawns" to "இறால்",
        "Squid" to "கணவாய்",
        "Crab" to "நண்டு",
        "Lobster" to "இரால்",
        "Combos" to "கூட்டு"
    )

    const val DELIVERY_CHARGE = 5000.0
    const val FREE_DELIVERY_MIN = 20000.0

    val ADMIN_EMAILS = setOf(
        "godofwarvetri999@gmail.com",
        "khanriyash1@gmail.com",
        "rameswarandryfish1@gmail.com"
    )
}
