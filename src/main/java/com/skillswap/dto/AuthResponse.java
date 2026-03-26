package com.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response body returned after a successful register or login.
 * In this MVP a simple opaque token (userId encoded) is returned.
 * Swap the 'token' field for a real JWT string when adding full JWT auth.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** Simple bearer token (userId-based for MVP; replace with JWT in production). */
    private String token;

    private Long   userId;
    private String name;
    private String email;
}
