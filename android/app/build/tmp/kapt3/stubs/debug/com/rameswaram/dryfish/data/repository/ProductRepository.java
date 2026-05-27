package com.rameswaram.dryfish.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0082@\u00a2\u0006\u0002\u0010\u0010J\u001a\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0013J\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0082@\u00a2\u0006\u0002\u0010\u0013J\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0082@\u00a2\u0006\u0002\u0010\u0013J\u001e\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0082@\u00a2\u0006\u0002\u0010\u0019J\u0012\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\u001bJ\u001a\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u000e0\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0013J\u001a\u0010\u001e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0013J\u001c\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00122\u0006\u0010 \u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019JH\u0010!\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\u00122\b\b\u0002\u0010\"\u001a\u00020#2\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u00182\n\b\u0002\u0010$\u001a\u0004\u0018\u00010\u00182\n\b\u0002\u0010%\u001a\u0004\u0018\u00010\u0018H\u0086@\u00a2\u0006\u0002\u0010&J\"\u0010\'\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\u00122\u0006\u0010(\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019J\f\u0010)\u001a\u00020**\u00020\u000fH\u0002J\f\u0010+\u001a\u00020\u000f*\u00020*H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lcom/rameswaram/dryfish/data/repository/ProductRepository;", "", "apiService", "Lcom/rameswaram/dryfish/data/api/ApiService;", "productCacheDao", "Lcom/rameswaram/dryfish/data/local/ProductCacheDao;", "(Lcom/rameswaram/dryfish/data/api/ApiService;Lcom/rameswaram/dryfish/data/local/ProductCacheDao;)V", "cacheDuration", "", "gson", "Lcom/google/gson/Gson;", "cacheProducts", "", "products", "", "Lcom/rameswaram/dryfish/domain/model/Product;", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getBestsellers", "Lcom/rameswaram/dryfish/utils/Resource;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCachedBestsellers", "getCachedFeatured", "getCachedProducts", "category", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCachedProductsFlow", "Lkotlinx/coroutines/flow/Flow;", "getCategories", "Lcom/rameswaram/dryfish/domain/model/Category;", "getFeaturedProducts", "getProductBySlug", "slug", "getProducts", "page", "", "search", "sort", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchProducts", "query", "toCacheEntity", "Lcom/rameswaram/dryfish/data/local/ProductCacheEntity;", "toProduct", "app_debug"})
public final class ProductRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.data.api.ApiService apiService = null;
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.data.local.ProductCacheDao productCacheDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.gson.Gson gson = null;
    private final long cacheDuration = 1800000L;
    
    public ProductRepository(@org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.api.ApiService apiService, @org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.data.local.ProductCacheDao productCacheDao) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getProducts(int page, @org.jetbrains.annotations.Nullable()
    java.lang.String category, @org.jetbrains.annotations.Nullable()
    java.lang.String search, @org.jetbrains.annotations.Nullable()
    java.lang.String sort, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<? extends java.util.List<com.rameswaram.dryfish.domain.model.Product>>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getFeaturedProducts(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<? extends java.util.List<com.rameswaram.dryfish.domain.model.Product>>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getBestsellers(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<? extends java.util.List<com.rameswaram.dryfish.domain.model.Product>>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getProductBySlug(@org.jetbrains.annotations.NotNull()
    java.lang.String slug, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<com.rameswaram.dryfish.domain.model.Product>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object searchProducts(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<? extends java.util.List<com.rameswaram.dryfish.domain.model.Product>>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCategories(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.rameswaram.dryfish.utils.Resource<? extends java.util.List<com.rameswaram.dryfish.domain.model.Category>>> $completion) {
        return null;
    }
    
    private final java.lang.Object cacheProducts(java.util.List<com.rameswaram.dryfish.domain.model.Product> products, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object getCachedProducts(java.lang.String category, kotlin.coroutines.Continuation<? super java.util.List<com.rameswaram.dryfish.domain.model.Product>> $completion) {
        return null;
    }
    
    private final java.lang.Object getCachedFeatured(kotlin.coroutines.Continuation<? super java.util.List<com.rameswaram.dryfish.domain.model.Product>> $completion) {
        return null;
    }
    
    private final java.lang.Object getCachedBestsellers(kotlin.coroutines.Continuation<? super java.util.List<com.rameswaram.dryfish.domain.model.Product>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.rameswaram.dryfish.domain.model.Product>> getCachedProductsFlow() {
        return null;
    }
    
    private final com.rameswaram.dryfish.data.local.ProductCacheEntity toCacheEntity(com.rameswaram.dryfish.domain.model.Product $this$toCacheEntity) {
        return null;
    }
    
    private final com.rameswaram.dryfish.domain.model.Product toProduct(com.rameswaram.dryfish.data.local.ProductCacheEntity $this$toProduct) {
        return null;
    }
}