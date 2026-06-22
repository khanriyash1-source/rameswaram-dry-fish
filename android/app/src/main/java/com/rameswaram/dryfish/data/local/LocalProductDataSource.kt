package com.rameswaram.dryfish.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rameswaram.dryfish.domain.model.Category
import com.rameswaram.dryfish.domain.model.Product

class LocalProductDataSource(private val context: Context) {
    private val gson = Gson()

    private var products: List<Product>? = null
    private var categories: List<Category>? = null

    fun getProducts(): List<Product> {
        if (products == null) load()
        return products ?: emptyList()
    }

    fun getFeaturedProducts(): List<Product> = getProducts().filter { it.isFeatured }

    fun getBestsellers(): List<Product> = getProducts().filter { it.isBestseller || it.isFeatured }

    fun getProductBySlug(slug: String): Product? = getProducts().find { it.slug == slug }

    fun getCategories(): List<Category> {
        if (categories == null) load()
        return categories ?: emptyList()
    }

    private fun load() {
        val json = context.assets.open("products.json").bufferedReader().use { it.readText() }
        val response = gson.fromJson(json, ProductsResponse::class.java)
        products = response.products.map { p ->
            p.copy(images = p.images.map { "file:///android_asset/images/$it" })
        }
        categories = response.categories
    }

    private data class ProductsResponse(
        val products: List<Product>,
        val categories: List<Category>
    )
}
