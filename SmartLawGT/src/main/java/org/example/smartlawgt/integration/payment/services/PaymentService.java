package org.example.smartlawgt.integration.payment.services;

import jakarta.servlet.http.HttpServletRequest;
import org.example.smartlawgt.integration.payment.dto.PurchaseRequestDTO;
import org.example.smartlawgt.integration.payment.dto.VNPayResponseDTO;
import java.util.Map;

public interface PaymentService {
    String createPaymentUrl(HttpServletRequest request, PurchaseRequestDTO requestDTO);
    VNPayResponseDTO processPaymentReturn(Map<String, String> params);
    VNPayResponseDTO processIPN(Map<String, String> params);
}
