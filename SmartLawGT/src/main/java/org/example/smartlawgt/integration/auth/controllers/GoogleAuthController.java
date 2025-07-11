// src/main/java/org/example/smartlawgt/integration/auth/controllers/GoogleAuthController.java
package org.example.smartlawgt.integration.auth.controllers;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.integration.auth.services.IGoogleAuthService;
import org.example.smartlawgt.query.dtos.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final IGoogleAuthService IGoogleAuthService;

    @PostMapping
    public ResponseEntity<LoginResponse> loginWithGoogle(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");
        LoginResponse loginResponse = IGoogleAuthService.verifyAndLogin(idToken);
        return ResponseEntity.ok(loginResponse);
    }
}
