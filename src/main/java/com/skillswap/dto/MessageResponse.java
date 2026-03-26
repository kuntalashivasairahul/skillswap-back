package com.skillswap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Read-model for a message, returned in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private Long         id;
    private UserResponse sender;
    private UserResponse receiver;
    private String       message;
    private String       timestamp;
}
