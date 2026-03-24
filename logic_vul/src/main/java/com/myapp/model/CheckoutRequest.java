package com.myapp.model;

public class CheckoutRequest {
    private Double clientTotal;
    private Boolean markAsPaid;
    private Boolean skipInventoryCheck;
    private String paymentReference;

    public Double getClientTotal() {
        return clientTotal;
    }

    public void setClientTotal(Double clientTotal) {
        this.clientTotal = clientTotal;
    }

    public Boolean getMarkAsPaid() {
        return markAsPaid;
    }

    public void setMarkAsPaid(Boolean markAsPaid) {
        this.markAsPaid = markAsPaid;
    }

    public Boolean getSkipInventoryCheck() {
        return skipInventoryCheck;
    }

    public void setSkipInventoryCheck(Boolean skipInventoryCheck) {
        this.skipInventoryCheck = skipInventoryCheck;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
}
