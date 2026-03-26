    package com.skillswap.service;

    import com.skillswap.dto.ChatRequest;
    import com.skillswap.dto.ChatResponse;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.HttpEntity;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.web.client.RestTemplate;

    import java.util.List;
    import java.util.Map;

    /**
     * Chatbot service backed by Google Gemini REST API.
     */
    @Slf4j
    @Service
    public class ChatbotService {

        private static final String FALLBACK_REPLY = "AI service is currently unavailable.";
        private static final String GEMINI_ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

        private final RestTemplate restTemplate = new RestTemplate();

        /**
         * Sends the user message to Gemini and returns the AI-generated reply.
         */
        public ChatResponse chat(ChatRequest request) {
            String apiKey = System.getenv("GEMINI_API_KEY");
            if (apiKey == null || apiKey.isBlank()) {
                log.warn("GEMINI_API_KEY is not set.");
                return ChatResponse.builder().reply(FALLBACK_REPLY).build();
            }

            String userMessage = request != null && request.getMessage() != null
                    ? request.getMessage().trim()
                    : "";

    String prompt = """
    Give a short concise structured answer.

    Format:
    ### Overview
    ### Learning Path
    ### Related Skills

    Keep each section short (2–4 points max).
    No long paragraphs.

    User:
    """ + userMessage;

            Map<String, Object> body = Map.of(
        "contents", List.of(
            Map.of(
                "parts", List.of(
                    Map.of("text", prompt)
                )
            )
        ),
        "generationConfig", Map.of(
            "maxOutputTokens", 1200,
            "temperature", 0.6,
            "topP", 0.9,
            "topK", 40
        ),
        "safetySettings", List.of()
    );

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

                ResponseEntity<Object> response = restTemplate.postForEntity(
                        GEMINI_ENDPOINT + apiKey,
                        entity,
                        Object.class
                );

                String reply;
                if (response.getBody() instanceof Map<?, ?> map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> typedMap = (Map<String, Object>) map;
                    log.info("Full raw Gemini API response body: {}", typedMap);
                    reply = extractReply(typedMap);
                } else {
                    log.info("Full raw Gemini API response body (non-map): {}", response.getBody());
                    reply = null;
                }

                log.info("Extracted reply length: {}", reply != null ? reply.length() : 0);

                if (reply == null || reply.isBlank()) {
                    return ChatResponse.builder().reply(FALLBACK_REPLY).build();
                }

                return ChatResponse.builder().reply(reply).build();
            } catch (Exception ex) {
                log.error("Gemini API call failed", ex);
                return ChatResponse.builder().reply(FALLBACK_REPLY).build();
            }
        }

        /**
         * Extracts candidate text from Gemini JSON response:
         * candidates[0].content.parts[*].text
         */
        private String extractReply(Map<String, Object> responseBody) {
            if (responseBody == null) {
                return null;
            }

            Object candidatesObj = responseBody.get("candidates");
            if (!(candidatesObj instanceof List<?> candidates) || candidates.isEmpty()) {
                return null;
            }

            Object firstCandidate = candidates.get(0);
            if (!(firstCandidate instanceof Map<?, ?> candidateMap)) {
                return null;
            }

            Object contentObj = candidateMap.get("content");
            if (!(contentObj instanceof Map<?, ?> contentMap)) {
                return null;
            }

            Object partsObj = contentMap.get("parts");
            if (!(partsObj instanceof List<?> parts) || parts.isEmpty()) {
                return null;
            }

            StringBuilder combinedText = new StringBuilder();
            for (Object part : parts) {
                if (part instanceof Map<?, ?> partMap) {
                    Object textObj = partMap.get("text");
                    if (textObj instanceof String text && !text.isBlank()) {
                        if (!combinedText.isEmpty()) {
                            combinedText.append("\n");
                        }
                        combinedText.append(text);
                    }
                }
            }

            return combinedText.isEmpty() ? null : combinedText.toString();
        }
    }