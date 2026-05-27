package com.rameswaram.dryfish.presentation.checkout;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\u0006\u0010\u0014\u001a\u00020\u0011J\u0006\u0010\u0015\u001a\u00020\u0011J\b\u0010\u0016\u001a\u00020\u0011H\u0002J\u001c\u0010\u0017\u001a\u00020\u00112\u0014\b\u0002\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u00110\u0019J\u000e\u0010\u001b\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u0010\u001c\u001a\u00020\u00112\u0006\u0010\u001d\u001a\u00020\u001eJ\u0006\u0010\u001f\u001a\u00020\u0011R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006 "}, d2 = {"Lcom/rameswaram/dryfish/presentation/checkout/CheckoutViewModel;", "Landroidx/lifecycle/ViewModel;", "cartRepository", "Lcom/rameswaram/dryfish/data/repository/CartRepository;", "orderRepository", "Lcom/rameswaram/dryfish/data/repository/OrderRepository;", "authRepository", "Lcom/rameswaram/dryfish/data/repository/AuthRepository;", "(Lcom/rameswaram/dryfish/data/repository/CartRepository;Lcom/rameswaram/dryfish/data/repository/OrderRepository;Lcom/rameswaram/dryfish/data/repository/AuthRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/rameswaram/dryfish/presentation/checkout/CheckoutUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "addAddress", "", "address", "Lcom/rameswaram/dryfish/domain/model/Address;", "clearError", "hideAddAddress", "loadCheckoutData", "placeOrder", "paymentCallback", "Lkotlin/Function1;", "", "selectAddress", "selectPaymentMethod", "method", "Lcom/rameswaram/dryfish/domain/model/PaymentMethod;", "showAddAddress", "app_debug"})
public final class CheckoutViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.data.repository.CartRepository cartRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.data.repository.OrderRepository orderRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.data.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.rameswaram.dryfish.presentation.checkout.CheckoutUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.rameswaram.dryfish.presentation.checkout.CheckoutUiState> uiState = null;
    
    public CheckoutViewModel(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.repository.CartRepository cartRepository, @org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.repository.OrderRepository orderRepository, @org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.repository.AuthRepository authRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.rameswaram.dryfish.presentation.checkout.CheckoutUiState> getUiState() {
        return null;
    }
    
    private final void loadCheckoutData() {
    }
    
    public final void selectAddress(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.domain.model.Address address) {
    }
    
    public final void selectPaymentMethod(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.domain.model.PaymentMethod method) {
    }
    
    public final void showAddAddress() {
    }
    
    public final void hideAddAddress() {
    }
    
    public final void addAddress(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.domain.model.Address address) {
    }
    
    public final void placeOrder(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> paymentCallback) {
    }
    
    public final void clearError() {
    }
}