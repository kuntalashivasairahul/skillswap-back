package com.skillswap.controller;

import com.skillswap.dto.SessionRequest;
import com.skillswap.dto.SessionResponse;
import com.skillswap.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints for booking and retrieving sessions.
 */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    /** Book a new session for an accepted match. */
    @PostMapping("/book")
    public ResponseEntity<SessionResponse> bookSession(@Valid @RequestBody SessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.bookSession(request));
    }

    /** List all sessions associated with a user. */
    @GetMapping("/{userId}")
    public ResponseEntity<List<SessionResponse>> getSessions(@PathVariable Long userId) {
        return ResponseEntity.ok(sessionService.getSessionsForUser(userId));
    }
}
