package com.example.glowskincareapp.Adapter;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    String ProductId,SellerId,Status,TransactionNo,TransactionScreenshot,DeliveryAddress,ProductName,ProductPrice,ProductImage;
    Date OrderDate;
    String CustomerId;

    public Order() {
    }

    public String getProductId() {
        return ProductId;
    }

    public String getSellerId() {
        return SellerId;
    }

    public String getStatus() {
        return Status;
    }

    public String getTransactionNo() {
        return TransactionNo;
    }

    public String getTransactionScreenshot() {
        return TransactionScreenshot;
    }

    public String getDeliveryAddress() {
        return DeliveryAddress;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public Date getOrderDate() {
        return OrderDate;
    }

    public Order(String productId, String sellerId, String status, String transactionNo, String transactionScreenshot, String deliveryAddress, String productName, String productPrice, String productImage, Date orderDate, String customerId) {
        ProductId = productId;
        SellerId = sellerId;
        Status = status;
        TransactionNo = transactionNo;
        TransactionScreenshot = transactionScreenshot;
        DeliveryAddress = deliveryAddress;
        ProductName = productName;
        ProductPrice = productPrice;
        ProductImage = productImage;
        OrderDate = orderDate;
        CustomerId = customerId;
    }
}
