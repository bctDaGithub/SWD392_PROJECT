package org.example.smartlawgt.integration.auth.services;

import org.example.smartlawgt.query.dtos.LoginResponse;

public interface IGoogleAuthService {
    LoginResponse verifyAndLogin(String idToken);
}
