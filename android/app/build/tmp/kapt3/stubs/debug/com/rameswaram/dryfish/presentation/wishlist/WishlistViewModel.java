package com.rameswaram.dryfish.presentation.wishlist;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\f\u001a\u00020\rJ\u000e\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\u0010R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/rameswaram/dryfish/presentation/wishlist/WishlistViewModel;", "Landroidx/lifecycle/ViewModel;", "wishlistRepository", "Lcom/rameswaram/dryfish/data/repository/WishlistRepository;", "(Lcom/rameswaram/dryfish/data/repository/WishlistRepository;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/rameswaram/dryfish/presentation/wishlist/WishlistUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "loadWishlist", "", "removeFromWishlist", "productId", "", "app_debug"})
public final class WishlistViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.data.repository.WishlistRepository wishlistRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.rameswaram.dryfish.presentation.wishlist.WishlistUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.rameswaram.dryfish.presentation.wishlist.WishlistUiState> uiState = null;
    
    public WishlistViewModel(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.repository.WishlistRepository wishlistRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.rameswaram.dryfish.presentation.wishlist.WishlistUiState> getUiState() {
        return null;
    }
    
    public final void loadWishlist() {
    }
    
    public final void removeFromWishlist(@org.jetbrains.annotations.NotNull()
    java.lang.String productId) {
    }
}