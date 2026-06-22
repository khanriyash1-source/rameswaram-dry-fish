package com.rameswaram.dryfish.data.repository

import com.rameswaram.dryfish.data.api.ApiService
import com.rameswaram.dryfish.domain.model.Order
import com.rameswaram.dryfish.domain.model.PaymentVerificationRequest
import com.rameswaram.dryfish.domain.model.RazorpayOrderResponse
import com.rameswaram.dryfish.utils.Resource

class OrderRepository(
    private val apiService: ApiService,
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) {
    private fun currentUserId(): String = authRepository.getSavedUid().orEmpty()

    suspend fun createOrder(orderData: Map<String, Any>): Resource<Order> {
        return try {
            val response = apiService.createOrder(orderData + mapOf("userId" to currentUserId()))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                val order = body.data ?: return Resource.Error("Empty order data")
                firestoreRepository.saveOrder(currentUserId(), order)
                firestoreRepository.updateSpending(currentUserId(), order.total)
                Resource.Success(order)
            } else {
                Resource.Error(body?.message ?: "Failed to create order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun getOrders(): Resource<List<Order>> {
        return try {
            val firestoreResult = firestoreRepository.loadOrders(currentUserId())
            if (firestoreResult is Resource.Success && firestoreResult.data.isNotEmpty()) {
                return Resource.Success(firestoreResult.data)
            }
            val response = apiService.getOrders(currentUserId())
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                val orders = body.data ?: emptyList()
                orders.forEach { firestoreRepository.saveOrder(currentUserId(), it) }
                Resource.Success(orders)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to load orders")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun getOrderDetails(orderId: String): Resource<Order> {
        return try {
            val firestoreResult = firestoreRepository.getOrder(currentUserId(), orderId)
            if (firestoreResult is Resource.Success && firestoreResult.data != null) {
                return Resource.Success(firestoreResult.data)
            }
            val response = apiService.getOrderDetails(orderId, currentUserId())
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                val order = body.data
                if (order != null) {
                    firestoreRepository.saveOrder(currentUserId(), order)
                    Resource.Success(order)
                } else Resource.Error("Order not found")
            } else {
                Resource.Error(response.body()?.message ?: "Order not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun createRazorpayOrder(totalPaise: Long): Resource<RazorpayOrderResponse> {
        return try {
            val response = apiService.createRazorpayOrder(mapOf("amount" to totalPaise))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                val data = body.data ?: return Resource.Error("Empty payment order data")
                Resource.Success(data)
            } else {
                Resource.Error(response.body()?.message ?: "Failed to create payment order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }

    suspend fun verifyPayment(request: PaymentVerificationRequest): Resource<Unit> {
        return try {
            val response = apiService.verifyPayment(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.body()?.message ?: "Payment verification failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}
