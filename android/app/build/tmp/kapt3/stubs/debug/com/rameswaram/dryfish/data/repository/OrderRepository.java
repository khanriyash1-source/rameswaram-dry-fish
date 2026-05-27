package com.rameswaram.dryfish.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J(\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\tH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u001c\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\r\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u001a\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00100\u0006H\u0086@\u00a2\u0006\u0002\u0010\u0011R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/rameswaram/dryfish/data/repository/OrderRepository;", "", "apiService", "Lcom/rameswaram/dryfish/data/api/ApiService;", "(Lcom/rameswaram/dryfish/data/api/ApiService;)V", "createOrder", "Lcom/rameswaram/dryfish/utils/Resource;", "Lcom/rameswaram/dryfish/domain/model/Order;", "orderData", "", "", "(Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getOrderDetails", "orderId", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getOrders", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class OrderRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.data.api.ApiService apiService = null;
    
    public OrderRepository(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.api.ApiService apiService) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createOrder(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> orderData, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<com.rameswaram.dryfish.domain.model.Order>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getOrders(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<? extends java.util.List<com.rameswaram.dryfish.domain.model.Order>>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getOrderDetails(@org.jetbrains.annotations.NotNull()
    java.lang.String orderId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<com.rameswaram.dryfish.domain.model.Order>> $completion) {
        return null;
    }
}