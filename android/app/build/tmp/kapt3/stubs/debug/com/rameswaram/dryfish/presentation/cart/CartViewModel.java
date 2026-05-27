package com.rameswaram.dryfish.presentation.cart;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\rJ\u0006\u0010\u000f\u001a\u00020\rJ\b\u0010\u0010\u001a\u00020\rH\u0002J\u000e\u0010\u0011\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u0014\u001a\u00020\r2\u0006\u0010\u0015\u001a\u00020\u0013J\u0016\u0010\u0016\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0018R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0019"}, d2 = {"Lcom/rameswaram/dryfish/presentation/cart/CartViewModel;", "Landroidx/lifecycle/ViewModel;", "cartRepository", "Lcom/rameswaram/dryfish/data/repository/CartRepository;", "(Lcom/rameswaram/dryfish/data/repository/CartRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/rameswaram/dryfish/presentation/cart/CartUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "applyCoupon", "", "clearCart", "clearError", "loadCartItems", "removeItem", "cartItemId", "", "updateCoupon", "code", "updateQuantity", "quantity", "", "app_debug"})
public final class CartViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.data.repository.CartRepository cartRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.rameswaram.dryfish.presentation.cart.CartUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.rameswaram.dryfish.presentation.cart.CartUiState> uiState = null;
    
    public CartViewModel(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.repository.CartRepository cartRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.rameswaram.dryfish.presentation.cart.CartUiState> getUiState() {
        return null;
    }
    
    private final void loadCartItems() {
    }
    
    public final void updateQuantity(@org.jetbrains.annotations.NotNull()
    java.lang.String cartItemId, int quantity) {
    }
    
    public final void removeItem(@org.jetbrains.annotations.NotNull()
    java.lang.String cartItemId) {
    }
    
    public final void clearCart() {
    }
    
    public final void updateCoupon(@org.jetbrains.annotations.NotNull()
    java.lang.String code) {
    }
    
    public final void applyCoupon() {
    }
    
    public final void clearError() {
    }
}