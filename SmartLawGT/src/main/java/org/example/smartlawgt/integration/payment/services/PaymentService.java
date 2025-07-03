package org.example.smartlawgt.integration.payment.services;

import jakarta.servlet.http.HttpServletRequest;
import org.example.smartlawgt.integration.payment.dtos.PurchasePaymentRequestDTO;
import org.example.smartlawgt.integration.payment.dtos.VNPayResponseDTO;
import java.util.Map;

public interface PaymentService {
    String createPaymentUrl(HttpServletRequest request, PurchasePaymentRequestDTO requestDTO);
    VNPayResponseDTO processPaymentReturn(Map<String, String> params);
    VNPayResponseDTO processIPN(Map<String, String> params);
}
