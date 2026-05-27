package com.rameswaram.dryfish.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\bH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010J\u0012\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00130\u0010J\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\u0010J\u001c\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\r0\b2\u0006\u0010\u0017\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u001a\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00130\bH\u0086@\u00a2\u0006\u0002\u0010\u000eJ$\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u001c\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u001dR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2 = {"Lcom/rameswaram/dryfish/data/repository/CartRepository;", "", "apiService", "Lcom/rameswaram/dryfish/data/api/ApiService;", "cartDao", "Lcom/rameswaram/dryfish/data/local/CartDao;", "(Lcom/rameswaram/dryfish/data/api/ApiService;Lcom/rameswaram/dryfish/data/local/CartDao;)V", "addToCart", "Lcom/rameswaram/dryfish/utils/Resource;", "Lcom/rameswaram/dryfish/domain/model/CartItem;", "item", "(Lcom/rameswaram/dryfish/domain/model/CartItem;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearCart", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCartItemCount", "Lkotlinx/coroutines/flow/Flow;", "", "getCartItems", "", "getCartTotal", "", "removeFromCart", "id", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "syncWithServer", "updateQuantity", "quantity", "(Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class CartRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.data.api.ApiService apiService = null;
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.data.local.CartDao cartDao = null;
    
    public CartRepository(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.api.ApiService apiService, @org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.local.CartDao cartDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.rameswaram.dryfish.domain.model.CartItem>> getCartItems() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Double> getCartTotal() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.Integer> getCartItemCount() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addToCart(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.domain.model.CartItem item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<com.rameswaram.dryfish.domain.model.CartItem>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateQuantity(@org.jetbrains.annotations.NotNull()
    java.lang.String id, int quantity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<com.rameswaram.dryfish.domain.model.CartItem>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object removeFromCart(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<kotlin.Unit>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object clearCart(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<kotlin.Unit>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object syncWithServer(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<? extends java.util.List<com.rameswaram.dryfish.domain.model.CartItem>>> $completion) {
        return null;
    }
}