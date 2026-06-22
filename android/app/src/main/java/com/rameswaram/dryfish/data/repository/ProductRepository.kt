package com.rameswaram.dryfish.data.repository

import com.rameswaram.dryfish.data.local.LocalProductDataSource
import com.rameswaram.dryfish.domain.model.Category
import com.rameswaram.dryfish.domain.model.Product
import com.rameswaram.dryfish.utils.Resource

class ProductRepository(
    private val localDataSource: LocalProductDataSource
) {
    fun getProducts(
        page: Int = 1,
        category: String? = null,
        search: String? = null,
        sort: String? = null
    ): Resource<List<Product>> {
        var products = localDataSource.getProducts()
        if (category != null) {
            products = products.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (!search.isNullOrBlank()) {
            val q = search.lowercase()
            products = products.filter {
                it.name.lowercase().contains(q) ||
                it.nameTamil?.lowercase()?.contains(q) == true ||
                it.shortDesc?.lowercase()?.contains(q) == true
            }
        }
        if (sort != null) {
            products = when (sort) {
                "price_asc" -> products.sortedBy { it.price }
                "price_desc" -> products.sortedByDescending { it.price }
                "name" -> products.sortedBy { it.name }
                "rating" -> products.sortedByDescending { it.rating }
                else -> products
            }
        }
        return Resource.Success(products)
    }

    fun getFeaturedProducts(): Resource<List<Product>> {
        return Resource.Success(localDataSource.getFeaturedProducts())
    }

    fun getBestsellers(): Resource<List<Product>> {
        return Resource.Success(localDataSource.getBestsellers())
    }

    fun getProductBySlug(slug: String): Resource<Product> {
        val product = localDataSource.getProductBySlug(slug)
        return if (product != null) Resource.Success(product)
        else Resource.Error("Product not found")
    }

    fun searchProducts(query: String): Resource<List<Product>> {
        return getProducts(search = query)
    }

    fun getCategories(): Resource<List<Category>> {
        return Resource.Success(localDataSource.getCategories())
    }
}
