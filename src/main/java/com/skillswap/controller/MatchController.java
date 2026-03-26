package com.skillswap.controller;

import com.skillswap.dto.MatchRequest;
import com.skillswap.dto.MatchResponse;
import com.skillswap.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints for matchmaking and match request lifecycle.
 */
@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    /**
     * Return compatible users for a given user based on bidirectional skill overlap.
     */
    @GetMapping("/find/{userId}")
    public ResponseEntity<List<MatchResponse>> findMatches(@PathVariable Long userId) {
        return ResponseEntity.ok(matchService.findMatches(userId));
    }

    /** Create a new pending match request. */
    @PostMapping("/request")
    public ResponseEntity<MatchResponse> requestMatch(@Valid @RequestBody MatchRequest request) {
        MatchResponse response = matchService.requestMatch(request.getRequesterId(), request.getRecipientId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** List all matches where user participates. */
    @GetMapping("/{userId}")
    public ResponseEntity<List<MatchResponse>> getMatchesForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(matchService.getMatchesForUser(userId));
    }

    /** Accept a pending match request. */
    @PutMapping("/accept/{matchId}")
    public ResponseEntity<MatchResponse> acceptMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchService.acceptMatch(matchId));
    }

    /** Reject a pending match request. */
    @PutMapping("/reject/{matchId}")
    public ResponseEntity<MatchResponse> rejectMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchService.rejectMatch(matchId));
    }
}
