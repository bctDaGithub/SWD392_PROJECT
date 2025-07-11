package org.example.smartlawgt.query.services.define;

import org.example.smartlawgt.query.dtos.LoginRequest;
import org.example.smartlawgt.query.dtos.LoginResponse;


public interface IAuthService {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse refreshToken(String refreshToken);
}