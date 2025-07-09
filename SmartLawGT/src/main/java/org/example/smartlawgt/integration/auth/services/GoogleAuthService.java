package org.example.smartlawgt.integration.auth.services;

import org.example.smartlawgt.query.dtos.LoginResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface GoogleAuthService {
    LoginResponse verifyAndLogin(String idToken);
}
