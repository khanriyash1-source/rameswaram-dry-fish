package com.rameswaram.dryfish.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_cache")
data class ProductCacheEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "slug")
    val slug: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "name_tamil")
    val nameTamil: String?,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "short_desc")
    val shortDesc: String?,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "images_json")
    val imagesJson: String,

    @ColumnInfo(name = "skus_json")
    val skusJson: String,

    @ColumnInfo(name = "tags_json")
    val tagsJson: String,

    @ColumnInfo(name = "is_featured")
    val isFeatured: Boolean,

    @ColumnInfo(name = "is_bestseller")
    val isBestseller: Boolean,

    @ColumnInfo(name = "rating")
    val rating: Double,

    @ColumnInfo(name = "review_count")
    val reviewCount: Int,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()
)
