package com.skillswap.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request model for AI chatbot endpoint.
 */
@Data
public class ChatRequest {

    @NotBlank(message = "Message is required")
    private String message;
}
