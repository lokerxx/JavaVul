package com.myapp.model;

public class OrderRecord {
    private Long id;
    private String orderNumber;
    private Long ownerUserId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private String status;
    private boolean inventoryLocked;

    public OrderRecord() {
    }

    public OrderRecord(Long id, String orderNumber, Long ownerUserId, String productName, int quantity, double unitPrice,
                       String status, boolean inventoryLocked) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.ownerUserId = ownerUserId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.status = status;
        this.inventoryLocked = inventoryLocked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isInventoryLocked() {
        return inventoryLocked;
    }

    public void setInventoryLocked(boolean inventoryLocked) {
        this.inventoryLocked = inventoryLocked;
    }

    public double serverTotal() {
        return quantity * unitPrice;
    }
}
