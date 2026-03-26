package com.skillswap.controller;

import com.skillswap.dto.RatingRequest;
import com.skillswap.dto.RatingResponse;
import com.skillswap.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints for submitting and reading user ratings.
 */
@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    /** Submit a new rating for another user. */
    @PostMapping
    public ResponseEntity<RatingResponse> submitRating(@Valid @RequestBody RatingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.submitRating(request));
    }

    /** List all ratings received by the given user. */
    @GetMapping("/{userId}")
    public ResponseEntity<List<RatingResponse>> getRatings(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getRatingsForUser(userId));
    }
}
