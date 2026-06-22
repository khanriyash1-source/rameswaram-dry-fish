package com.rameswaram.dryfish.domain.model

data class RazorpayOrderResponse(
    val orderId: String,
    val amount: Long,
    val currency: String,
    val keyId: String
)

data class PaymentVerificationRequest(
    val razorpay_order_id: String,
    val razorpay_payment_id: String,
    val razorpay_signature: String
)
