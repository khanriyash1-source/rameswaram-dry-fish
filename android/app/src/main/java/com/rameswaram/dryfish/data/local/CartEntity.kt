package com.rameswaram.dryfish.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rameswaram.dryfish.domain.model.CartItem

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "product_id")
    val productId: String,

    @ColumnInfo(name = "product_name")
    val productName: String,

    @ColumnInfo(name = "product_image")
    val productImage: String,

    @ColumnInfo(name = "selected_sku_id")
    val selectedSkuId: String,

    @ColumnInfo(name = "weight")
    val weight: String,

    @ColumnInfo(name = "price")
    val price: Double,

    @ColumnInfo(name = "mrp")
    val mrp: Double,

    @ColumnInfo(name = "quantity")
    val quantity: Int,

    @ColumnInfo(name = "max_quantity")
    val maxQuantity: Int,

    @ColumnInfo(name = "product_slug")
    val productSlug: String
) {
    fun toCartItem(): CartItem = CartItem(
        id = id,
        productId = productId,
        productName = productName,
        productImage = productImage,
        selectedSkuId = selectedSkuId,
        weight = weight,
        price = price,
        mrp = mrp,
        quantity = quantity,
        maxQuantity = maxQuantity,
        productSlug = productSlug
    )

    companion object {
        fun fromCartItem(item: CartItem): CartEntity = CartEntity(
            id = item.id,
            productId = item.productId,
            productName = item.productName,
            productImage = item.productImage,
            selectedSkuId = item.selectedSkuId,
            weight = item.weight,
            price = item.price,
            mrp = item.mrp,
            quantity = item.quantity,
            maxQuantity = item.maxQuantity,
            productSlug = item.productSlug
        )
    }
}
