package org.example.smartlawgt.integration.payment.controllers;

import org.example.smartlawgt.command.entities.TransactionMethod;
import org.example.smartlawgt.integration.payment.dtos.PurchasePaymentRequestDTO;
import org.example.smartlawgt.integration.payment.dtos.VNPayResponseDTO;
import org.example.smartlawgt.integration.payment.services.PaymentFactory;
import org.example.smartlawgt.integration.payment.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/vnpay")
public class PaymentController {

    @Autowired
    private PaymentFactory paymentFactory;

    @PostMapping("/payment")
    public ResponseEntity<String> createPayment(HttpServletRequest request, @RequestBody PurchasePaymentRequestDTO requestDTO) {
        PaymentService paymentService = paymentFactory.getPaymentService(requestDTO.getTransactionMethod());
        String paymentUrl = paymentService.createPaymentUrl(request, requestDTO);
        return ResponseEntity.ok("redirect:" + paymentUrl);
    }

    @GetMapping("/return")
    public ResponseEntity<VNPayResponseDTO> paymentReturn(@RequestParam Map<String, String> vnp_Params) {
        PaymentService paymentService = paymentFactory.getPaymentService(TransactionMethod.VNPAY);
        VNPayResponseDTO response = paymentService.processPaymentReturn(vnp_Params);
        return ResponseEntity.status(response.getStatus().equals("SUCCESS") ? 200 : 400).body(response);
    }

    @PostMapping("/ipn")
    public ResponseEntity<VNPayResponseDTO> ipn(@RequestParam Map<String, String> vnp_Params) {
        PaymentService paymentService = paymentFactory.getPaymentService(TransactionMethod.VNPAY);
        VNPayResponseDTO response = paymentService.processIPN(vnp_Params);
        return ResponseEntity.ok(response);
    }
}
