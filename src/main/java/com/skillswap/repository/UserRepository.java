package com.skillswap.repository;

import com.skillswap.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data access layer for {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Find a user by their unique email address (used for login). */
    Optional<User> findByEmail(String email);

    /** Check if an email is already registered. */
    boolean existsByEmail(String email);
}
