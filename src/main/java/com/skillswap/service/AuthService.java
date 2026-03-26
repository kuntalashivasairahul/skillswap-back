package com.skillswap.service;

import com.skillswap.dto.AuthResponse;
import com.skillswap.dto.LoginRequest;
import com.skillswap.dto.RegisterRequest;
import com.skillswap.model.User;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles user registration and login logic.
 *
 * Security note: passwords are hashed with BCrypt before persistence;
 * plain-text passwords are never stored or logged.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user account.
     *
     * @throws IllegalArgumentException if the email is already taken
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                // Hash the password — BCrypt adds its own salt automatically
                .password(passwordEncoder.encode(request.getPassword()))
                .bio(request.getBio())
                .rating(0.0)
                .build();

        User saved = userRepository.save(user);

        return buildAuthResponse(saved);
    }

    /**
     * Authenticate a user with email + password.
     *
     * @throws IllegalArgumentException if credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Compare request password against stored BCrypt hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Build the auth response.  For MVP we use a simple token derived from
     * the userId.  Replace with a proper JWT (io.jsonwebtoken) for production.
     */
    private AuthResponse buildAuthResponse(User user) {
        String token = "token-" + user.getId();   // TODO: replace with JWT
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
