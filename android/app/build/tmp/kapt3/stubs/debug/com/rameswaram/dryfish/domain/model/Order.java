package com.rameswaram.dryfish.domain.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b3\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u009f\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0006\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\u0010\u001a\u00020\u0003\u0012\u0006\u0010\u0011\u001a\u00020\u0012\u0012\u0006\u0010\u0013\u001a\u00020\u0012\u0012\u0006\u0010\u0014\u001a\u00020\u0012\u0012\u0006\u0010\u0015\u001a\u00020\u0012\u0012\b\u0010\u0016\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0017\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0018\u001a\u00020\u0003\u0012\u0006\u0010\u0019\u001a\u00020\u0003\u0012\b\u0010\u001a\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u001bJ\t\u00103\u001a\u00020\u0003H\u00c6\u0003J\t\u00104\u001a\u00020\u0012H\u00c6\u0003J\t\u00105\u001a\u00020\u0012H\u00c6\u0003J\t\u00106\u001a\u00020\u0012H\u00c6\u0003J\u000b\u00107\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u00108\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u00109\u001a\u00020\u0003H\u00c6\u0003J\t\u0010:\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010;\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010<\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010=\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u00c6\u0003J\t\u0010>\u001a\u00020\tH\u00c6\u0003J\u000f\u0010?\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0006H\u00c6\u0003J\t\u0010@\u001a\u00020\rH\u00c6\u0003J\t\u0010A\u001a\u00020\u000fH\u00c6\u0003J\t\u0010B\u001a\u00020\u0003H\u00c6\u0003J\t\u0010C\u001a\u00020\u0012H\u00c6\u0003J\u00c5\u0001\u0010D\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\b\u0002\u0010\b\u001a\u00020\t2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00062\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00032\b\b\u0002\u0010\u0011\u001a\u00020\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u00122\b\b\u0002\u0010\u0014\u001a\u00020\u00122\b\b\u0002\u0010\u0015\u001a\u00020\u00122\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0017\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0018\u001a\u00020\u00032\b\b\u0002\u0010\u0019\u001a\u00020\u00032\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010E\u001a\u00020F2\b\u0010G\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010H\u001a\u00020IH\u00d6\u0001J\t\u0010J\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0018\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001dR\u0011\u0010\u0013\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\u0014\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010 R\u0013\u0010\u001a\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001dR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001dR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0013\u0010\u0017\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001dR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u001dR\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010)R\u0011\u0010\u0010\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u001dR\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010,R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010.R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010 R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u0010%R\u0011\u0010\u0015\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u0010 R\u0011\u0010\u0019\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\u001d\u00a8\u0006K"}, d2 = {"Lcom/rameswaram/dryfish/domain/model/Order;", "", "id", "", "orderNumber", "items", "", "Lcom/rameswaram/dryfish/domain/model/OrderItem;", "status", "Lcom/rameswaram/dryfish/domain/model/OrderStatus;", "timeline", "Lcom/rameswaram/dryfish/domain/model/OrderTimeline;", "shippingAddress", "Lcom/rameswaram/dryfish/domain/model/Address;", "paymentMethod", "Lcom/rameswaram/dryfish/domain/model/PaymentMethod;", "paymentStatus", "subtotal", "", "deliveryCharge", "discount", "total", "couponApplied", "notes", "createdAt", "updatedAt", "estimatedDelivery", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Lcom/rameswaram/dryfish/domain/model/OrderStatus;Ljava/util/List;Lcom/rameswaram/dryfish/domain/model/Address;Lcom/rameswaram/dryfish/domain/model/PaymentMethod;Ljava/lang/String;DDDDLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getCouponApplied", "()Ljava/lang/String;", "getCreatedAt", "getDeliveryCharge", "()D", "getDiscount", "getEstimatedDelivery", "getId", "getItems", "()Ljava/util/List;", "getNotes", "getOrderNumber", "getPaymentMethod", "()Lcom/rameswaram/dryfish/domain/model/PaymentMethod;", "getPaymentStatus", "getShippingAddress", "()Lcom/rameswaram/dryfish/domain/model/Address;", "getStatus", "()Lcom/rameswaram/dryfish/domain/model/OrderStatus;", "getSubtotal", "getTimeline", "getTotal", "getUpdatedAt", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class Order {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String orderNumber = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.rameswaram.dryfish.domain.model.OrderItem> items = null;
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.domain.model.OrderStatus status = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.rameswaram.dryfish.domain.model.OrderTimeline> timeline = null;
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.domain.model.Address shippingAddress = null;
    @org.jetbrains.annotations.NotNull()
    private final com.rameswaram.dryfish.domain.model.PaymentMethod paymentMethod = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String paymentStatus = null;
    private final double subtotal = 0.0;
    private final double deliveryCharge = 0.0;
    private final double discount = 0.0;
    private final double total = 0.0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String couponApplied = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String notes = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String createdAt = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String updatedAt = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String estimatedDelivery = null;
    
    public Order(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String orderNumber, @org.jetbrains.annotations.NotNull()
    java.util.List<com.rameswaram.dryfish.domain.model.OrderItem> items, @org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.domain.model.OrderStatus status, @org.jetbrains.annotations.NotNull()
    java.util.List<com.rameswaram.dryfish.domain.model.OrderTimeline> timeline, @org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.domain.model.Address shippingAddress, @org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.domain.model.PaymentMethod paymentMethod, @org.jetbrains.annotations.NotNull()
    java.lang.String paymentStatus, double subtotal, double deliveryCharge, double discount, double total, @org.jetbrains.annotations.Nullable()
    java.lang.String couponApplied, @org.jetbrains.annotations.Nullable()
    java.lang.String notes, @org.jetbrains.annotations.NotNull()
    java.lang.String createdAt, @org.jetbrains.annotations.NotNull()
    java.lang.String updatedAt, @org.jetbrains.annotations.Nullable()
    java.lang.String estimatedDelivery) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOrderNumber() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.rameswaram.dryfish.domain.model.OrderItem> getItems() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.rameswaram.dryfish.domain.model.OrderStatus getStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.rameswaram.dryfish.domain.model.OrderTimeline> getTimeline() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.rameswaram.dryfish.domain.model.Address getShippingAddress() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.rameswaram.dryfish.domain.model.PaymentMethod getPaymentMethod() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPaymentStatus() {
        return null;
    }
    
    public final double getSubtotal() {
        return 0.0;
    }
    
    public final double getDeliveryCharge() {
        return 0.0;
    }
    
    public final double getDiscount() {
        return 0.0;
    }
    
    public final double getTotal() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCouponApplied() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getNotes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCreatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUpdatedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getEstimatedDelivery() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final double component10() {
        return 0.0;
    }
    
    public final double component11() {
        return 0.0;
    }
    
    public final double component12() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component15() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component16() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component17() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.rameswaram.dryfish.domain.model.OrderItem> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.rameswaram.dryfish.domain.model.OrderStatus component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.rameswaram.dryfish.domain.model.OrderTimeline> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.rameswaram.dryfish.domain.model.Address component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.rameswaram.dryfish.domain.model.PaymentMethod component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component8() {
        return null;
    }
    
    public final double component9() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.rameswaram.dryfish.domain.model.Order copy(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String orderNumber, @org.jetbrains.annotations.NotNull()
    java.util.List<com.rameswaram.dryfish.domain.model.OrderItem> items, @org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.domain.model.OrderStatus status, @org.jetbrains.annotations.NotNull()
    java.util.List<com.rameswaram.dryfish.domain.model.OrderTimeline> timeline, @org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.domain.model.Address shippingAddress, @org.jetbrains.annotations.NotNull()
    com.rameswaram.dryfish.domain.model.PaymentMethod paymentMethod, @org.jetbrains.annotations.NotNull()
    java.lang.String paymentStatus, double subtotal, double deliveryCharge, double discount, double total, @org.jetbrains.annotations.Nullable()
    java.lang.String couponApplied, @org.jetbrains.annotations.Nullable()
    java.lang.String notes, @org.jetbrains.annotations.NotNull()
    java.lang.String createdAt, @org.jetbrains.annotations.NotNull()
    java.lang.String updatedAt, @org.jetbrains.annotations.Nullable()
    java.lang.String estimatedDelivery) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}