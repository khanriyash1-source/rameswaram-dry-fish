package com.rameswaram.dryfish.data.repository

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

sealed class RazorpayEvent {
    data class Success(val paymentId: String) : RazorpayEvent()
    data class Error(val code: Int, val response: String) : RazorpayEvent()
}

object RazorpayPaymentBus {
    private val _events = Channel<RazorpayEvent>(Channel.CONFLATED)
    val events: Flow<RazorpayEvent> = _events.receiveAsFlow()

    fun emit(event: RazorpayEvent) {
        _events.trySend(event)
    }

    fun clear() {
        _events.tryReceive()
    }
}
