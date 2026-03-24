package com.myapp.model;

public class DiscountCalcRequest {
    private Double baseAmount;
    private Double couponAmount;
    private Double vipRate;
    private Double flashSaleRate;
    private Double pointsAmount;

    public Double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(Double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public Double getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(Double couponAmount) {
        this.couponAmount = couponAmount;
    }

    public Double getVipRate() {
        return vipRate;
    }

    public void setVipRate(Double vipRate) {
        this.vipRate = vipRate;
    }

    public Double getFlashSaleRate() {
        return flashSaleRate;
    }

    public void setFlashSaleRate(Double flashSaleRate) {
        this.flashSaleRate = flashSaleRate;
    }

    public Double getPointsAmount() {
        return pointsAmount;
    }

    public void setPointsAmount(Double pointsAmount) {
        this.pointsAmount = pointsAmount;
    }
}
