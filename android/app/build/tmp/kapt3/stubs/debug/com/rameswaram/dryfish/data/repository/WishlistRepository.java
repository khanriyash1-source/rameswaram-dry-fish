package com.rameswaram.dryfish.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\nJ\u001a\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\f0\u0006H\u0086@\u00a2\u0006\u0002\u0010\rJ\u001c\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00062\u0006\u0010\b\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/rameswaram/dryfish/data/repository/WishlistRepository;", "", "apiService", "Lcom/rameswaram/dryfish/data/api/ApiService;", "(Lcom/rameswaram/dryfish/data/api/ApiService;)V", "addToWishlist", "Lcom/rameswaram/dryfish/utils/Resource;", "Lcom/rameswaram/dryfish/domain/model/WishlistItem;", "productId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getWishlist", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFromWishlist", "", "app_debug"})
public final class WishlistRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.data.api.ApiService apiService = null;
    
    public WishlistRepository(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.api.ApiService apiService) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getWishlist(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<? extends java.util.List<com.rameswaram.dryfish.domain.model.WishlistItem>>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addToWishlist(@org.jetbrains.annotations.NotNull()
    java.lang.String productId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<com.rameswaram.dryfish.domain.model.WishlistItem>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object removeFromWishlist(@org.jetbrains.annotations.NotNull()
    java.lang.String productId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<kotlin.Unit>> $completion) {
        return null;
    }
}