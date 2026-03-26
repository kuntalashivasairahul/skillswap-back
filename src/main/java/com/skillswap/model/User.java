package com.skillswap.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a registered user on the SkillSwap platform.
 * Passwords are stored as BCrypt hashes — never plain-text.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Full display name of the user. */
    @Column(nullable = false, length = 100)
    private String name;

    /** Unique email address — used for login. */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** BCrypt-hashed password — never stored in plain text. */
    @Column(nullable = false)
    private String password;

    /** Short biography / introduction visible to other users. */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * Average rating computed whenever a new Rating is submitted.
     * Initialised to 0.0 for new users.
     */
    @Column(nullable = false)
    @Builder.Default
    private Double rating = 0.0;

    /** Timestamp of account creation — set automatically by Hibernate. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
