package com.rameswaram.dryfish.domain.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004J\u0006\u0010\u0005\u001a\u00020\u0006J\u0006\u0010\u0007\u001a\u00020\u0006j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000e\u00a8\u0006\u000f"}, d2 = {"Lcom/rameswaram/dryfish/domain/model/OrderStatus;", "", "(Ljava/lang/String;I)V", "displayName", "", "isActive", "", "isDelivered", "PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED", "RETURNED", "app_debug"})
public enum OrderStatus {
    /*public static final*/ PENDING /* = new PENDING() */,
    /*public static final*/ CONFIRMED /* = new CONFIRMED() */,
    /*public static final*/ PROCESSING /* = new PROCESSING() */,
    /*public static final*/ SHIPPED /* = new SHIPPED() */,
    /*public static final*/ DELIVERED /* = new DELIVERED() */,
    /*public static final*/ CANCELLED /* = new CANCELLED() */,
    /*public static final*/ RETURNED /* = new RETURNED() */;
    
    OrderStatus() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String displayName() {
        return null;
    }
    
    public final boolean isActive() {
        return false;
    }
    
    public final boolean isDelivered() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<com.rameswaram.dryfish.domain.model.OrderStatus> getEntries() {
        return null;
    }
}