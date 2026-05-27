package com.rameswaram.dryfish.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun String.toTamil(): String = this

fun Int.toRupees(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    format.maximumFractionDigits = 0
    return format.format(this / 100.0)
}

fun Double.toRupees(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    format.maximumFractionDigits = if (this == this.toLong().toDouble()) 0 else 2
    return format.format(this / 100.0)
}

fun String.formatDate(fromPattern: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", toPattern: String = "dd MMM yyyy"): String {
    return try {
        val sdf = SimpleDateFormat(fromPattern, Locale("en", "IN"))
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.parse(this)
        val output = SimpleDateFormat(toPattern, Locale("en", "IN"))
        output.timeZone = TimeZone.getDefault()
        date?.let { output.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun String.formatDateRelative(): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("en", "IN"))
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.parse(this) ?: return this
        val now = Date()
        val diff = now.time - date.time
        val minutes = diff / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24

        when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days < 7 -> "${days}d ago"
            else -> formatDate()
        }
    } catch (e: Exception) {
        this
    }
}
