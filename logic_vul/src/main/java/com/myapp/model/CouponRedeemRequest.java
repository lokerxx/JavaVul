package com.myapp.model;

public class CouponRedeemRequest {
    private Long actingUserId;
    private String couponCode;
    private Double orderAmount;
    private Integer redemptionCount;

    public Long getActingUserId() {
        return actingUserId;
    }

    public void setActingUserId(Long actingUserId) {
        this.actingUserId = actingUserId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Integer getRedemptionCount() {
        return redemptionCount;
    }

    public void setRedemptionCount(Integer redemptionCount) {
        this.redemptionCount = redemptionCount;
    }
}
