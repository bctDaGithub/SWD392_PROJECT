package org.example.smartlawgt.integration.payment.services;

import jakarta.servlet.http.HttpServletRequest;
import org.example.smartlawgt.integration.payment.dtos.PurchasePaymentRequestDTO;

import java.util.Map;

public interface PaymentService {
    String createPaymentUrl(HttpServletRequest request, PurchasePaymentRequestDTO requestDTO);
    Object processPaymentReturn(Map<String, String> params);
    Object processIPN(Map<String, String> params);
}
