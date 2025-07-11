package org.example.smartlawgt.query.services.implement;

import lombok.RequiredArgsConstructor;
import org.example.smartlawgt.command.entities.UserEntity;
import org.example.smartlawgt.command.repositories.UserRepository;
import org.example.smartlawgt.query.services.define.IAuthService;
import org.example.smartlawgt.integration.jwt.JwtUtil;
import org.example.smartlawgt.query.dtos.LoginRequest;
import org.example.smartlawgt.query.dtos.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail());

        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        if (!user.getIsActive()) {
            throw new IllegalStateException("Account is inactive");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String accessToken = jwtUtil.generateAccessToken(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getRole()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getRole()
        );

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .userName(user.getUserName())
                .role(user.getRole())
                .build();
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        UserEntity user = userRepository.findByEmail(email);

        if (user == null || !user.getIsActive()) {
            throw new IllegalStateException("User not found or inactive");
        }

        String newAccessToken = jwtUtil.generateAccessToken(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getRole()
        );

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .userName(user.getUserName())
                .role(user.getRole())
                .build();
    }
}
