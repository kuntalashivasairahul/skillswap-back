package com.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Public-facing user profile — never exposes the hashed password.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long   id;
    private String name;
    private String email;
    private String bio;
    private Double rating;
    private String createdAt;
}
