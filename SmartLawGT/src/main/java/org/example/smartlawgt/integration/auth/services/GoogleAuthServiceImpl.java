package org.example.smartlawgt.integration.auth.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.entities.UserEntity;
import org.example.smartlawgt.command.repositories.UserRepository;
import org.example.smartlawgt.command.services.define.IUserCommandService;
import org.example.smartlawgt.integration.jwt.JwtUtil;
import org.example.smartlawgt.query.dtos.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final IUserCommandService userCommandService;
    private final JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    public LoginResponse verifyAndLogin(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) throw new IllegalArgumentException("ID token không hợp lệ.");

            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String avatar = (String) payload.get("picture");

            // Tạo user nếu chưa có
            UserEntity user = userCommandService.findByEmail(email);
            if (user == null) {
                String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());

                user = userCommandService.createUser(
                        UserEntity.builder()
                                .userId(UUID.randomUUID())
                                .email(email)
                                .userName(email.split("@")[0])
                                .name(name)
                                .avatarUrlText(avatar)
                                .password(randomPassword) // ✅ Add this line
                                .isActive(true)
                                .role("USER")
                                .createdDate(LocalDateTime.now())
                                .build()
                );
            }


            String accessToken = jwtUtil.generateAccessToken(
                    user.getUserId(), user.getUserName(), user.getEmail(), user.getRole());

            String refreshToken = jwtUtil.generateRefreshToken(
                    user.getUserId(), user.getUserName(), user.getEmail(), user.getRole());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .email(user.getEmail())
                    .userName(user.getUserName())
                    .role(user.getRole())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Xác minh Google ID Token thất bại", e);
        }
    }
}

