package com.skillswap.controller;

import com.skillswap.dto.MessageRequest;
import com.skillswap.dto.MessageResponse;
import com.skillswap.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints for direct chat messages between matched users.
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /** Send a new message from sender to receiver. */
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.sendMessage(request));
    }

    /** Get full message thread associated with a match. */
    @GetMapping("/{matchId}")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long matchId) {
        return ResponseEntity.ok(messageService.getMessagesByMatch(matchId));
    }
}
