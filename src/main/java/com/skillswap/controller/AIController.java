package com.skillswap.controller;

import com.skillswap.dto.AIRecommendationResponse;
import com.skillswap.dto.ChatRequest;
import com.skillswap.dto.ChatResponse;
import com.skillswap.service.ChatbotService;
import com.skillswap.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AI feature endpoints.
 *
 * This controller is additive and does not modify existing SkillSwap APIs.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final RecommendationService recommendationService;
    private final ChatbotService chatbotService;

    /**
     * Recommend new skills for the given user.
     */
    @GetMapping("/recommend-skills/{userId}")
    public ResponseEntity<AIRecommendationResponse> recommendSkills(@PathVariable Long userId) {
        return ResponseEntity.ok(recommendationService.recommendSkills(userId));
    }

    /**
     * Placeholder chatbot endpoint.
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatbotService.chat(request));
    }
}
