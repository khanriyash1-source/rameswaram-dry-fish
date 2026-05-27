package com.rameswaram.dryfish.presentation.product;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\rJ\u0006\u0010\u0011\u001a\u00020\rJ\u000e\u0010\u0012\u001a\u00020\r2\u0006\u0010\u0013\u001a\u00020\u0014J\u000e\u0010\u0015\u001a\u00020\r2\u0006\u0010\u0016\u001a\u00020\u0017R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0018"}, d2 = {"Lcom/rameswaram/dryfish/presentation/product/ProductDetailViewModel;", "Landroidx/lifecycle/ViewModel;", "productRepository", "Lcom/rameswaram/dryfish/data/repository/ProductRepository;", "(Lcom/rameswaram/dryfish/data/repository/ProductRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/rameswaram/dryfish/presentation/product/ProductDetailUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "addToCart", "", "cartRepository", "Lcom/rameswaram/dryfish/data/repository/CartRepository;", "decrementQuantity", "incrementQuantity", "loadProduct", "slug", "", "selectSku", "sku", "Lcom/rameswaram/dryfish/domain/model/SKU;", "app_debug"})
public final class ProductDetailViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.data.repository.ProductRepository productRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.rameswaram.dryfish.presentation.product.ProductDetailUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.rameswaram.dryfish.presentation.product.ProductDetailUiState> uiState = null;
    
    public ProductDetailViewModel(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.repository.ProductRepository productRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.rameswaram.dryfish.presentation.product.ProductDetailUiState> getUiState() {
        return null;
    }
    
    public final void loadProduct(@org.jetbrains.annotations.NotNull()
    java.lang.String slug) {
    }
    
    public final void selectSku(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.domain.model.SKU sku) {
    }
    
    public final void incrementQuantity() {
    }
    
    public final void decrementQuantity() {
    }
    
    public final void addToCart(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.repository.CartRepository cartRepository) {
    }
}