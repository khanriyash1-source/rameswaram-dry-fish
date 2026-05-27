package com.rameswaram.dryfish.data.repository

import com.rameswaram.dryfish.data.api.ApiService
import com.rameswaram.dryfish.domain.model.Order
import com.rameswaram.dryfish.utils.Resource

class OrderRepository(
    private val apiService: ApiService
) {
    suspend fun createOrder(orderData: Map<String, Any>): Resource<Order> {
        return try {
            val response = apiService.createOrder(orderData)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to create order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun getOrders(): Resource<List<Order>> {
        return try {
            val response = apiService.getOrders()
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data ?: emptyList())
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load orders")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun getOrderDetails(orderId: String): Resource<Order> {
        return try {
            val response = apiService.getOrderDetails(orderId)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error(response.body()?.message ?: "Order not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}
