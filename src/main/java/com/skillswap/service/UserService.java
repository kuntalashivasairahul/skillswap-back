package com.skillswap.service;

import com.skillswap.dto.UserResponse;
import com.skillswap.model.User;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for user profile operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /** Retrieve a user's public profile by ID. */
    public UserResponse getUserById(Long id) {
        User user = findOrThrow(id);
        return toResponse(user);
    }

    /**
     * Update the mutable profile fields (name, bio).
     * Email and password changes are handled separately for security reasons.
     */
    @Transactional
    public UserResponse updateProfile(Long id, String name, String bio) {
        User user = findOrThrow(id);
        if (name != null && !name.isBlank()) user.setName(name);
        if (bio  != null)                    user.setBio(bio);
        return toResponse(userRepository.save(user));
    }

    // ── Package-private helpers used by other services ───────────────────────

    /** Load a User entity or throw a descriptive exception. */
    public User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    /** Map a User entity to its public DTO. */
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .bio(user.getBio())
                .rating(user.getRating())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .build();
    }
}
