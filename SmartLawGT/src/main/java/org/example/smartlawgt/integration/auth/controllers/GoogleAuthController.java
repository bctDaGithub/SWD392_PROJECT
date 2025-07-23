package org.example.smartlawgt.integration.auth.controllers;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.integration.auth.dtos.GoogleLoginRequest;
import org.example.smartlawgt.integration.auth.services.IGoogleAuthService;
import org.example.smartlawgt.query.dtos.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final IGoogleAuthService googleAuthService;

    @PostMapping
    public ResponseEntity<LoginResponse> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        LoginResponse loginResponse = googleAuthService.verifyAndLogin(request.getIdToken());
        return ResponseEntity.ok(loginResponse);
    }
}
