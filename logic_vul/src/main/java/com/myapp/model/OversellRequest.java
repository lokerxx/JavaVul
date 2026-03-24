package com.myapp.model;

public class OversellRequest {
    private Long actingUserId;
    private String skuCode;
    private Integer purchaseQuantity;
    private Integer parallelRequests;

    public Long getActingUserId() {
        return actingUserId;
    }

    public void setActingUserId(Long actingUserId) {
        this.actingUserId = actingUserId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public Integer getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(Integer purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public Integer getParallelRequests() {
        return parallelRequests;
    }

    public void setParallelRequests(Integer parallelRequests) {
        this.parallelRequests = parallelRequests;
    }
}
