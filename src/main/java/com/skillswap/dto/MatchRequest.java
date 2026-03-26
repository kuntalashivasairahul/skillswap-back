package com.skillswap.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Payload for sending a match request from one user to another.
 */
@Data
public class MatchRequest {

    @NotNull(message = "Requester ID is required")
    private Long requesterId;   // userA — the one sending the request

    @NotNull(message = "Recipient ID is required")
    private Long recipientId;   // userB — the one receiving the request
}
