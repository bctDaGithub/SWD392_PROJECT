package org.example.smartlawgt.integration.payment.dtos;


import org.example.smartlawgt.command.entities.TransactionMethod;

public class PurchasePaymentRequestDTO {
    private long amount;
    private String orderInfo;
    private TransactionMethod transactionMethod;

    // Getters and Setters
    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public TransactionMethod getTransactionMethod() {
        return transactionMethod;
    }

    public void setTransactionMethod(TransactionMethod transactionMethod) {
        this.transactionMethod = transactionMethod;
    }
}