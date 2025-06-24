package org.example.smartlawgt.integration.payment.services;

import org.example.smartlawgt.command.entities.TransactionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentFactory {

    @Autowired
    private VNPayService vnPayService;

    public PaymentService getPaymentService(TransactionMethod paymentMethod) {
        if (paymentMethod == TransactionMethod.VNPAY) {
            return vnPayService;
        }
        throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
    }
}
