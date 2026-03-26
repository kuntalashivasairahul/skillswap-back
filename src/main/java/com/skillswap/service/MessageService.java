package com.skillswap.service;

import com.skillswap.dto.MessageRequest;
import com.skillswap.dto.MessageResponse;
import com.skillswap.model.Match;
import com.skillswap.model.Message;
import com.skillswap.model.User;
import com.skillswap.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for direct messaging between matched users.
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService       userService;
    private final MatchService      matchService;

    /**
     * Persist a new message from sender to receiver.
     *
     * @throws IllegalArgumentException if either user does not exist
     */
    @Transactional
    public MessageResponse sendMessage(MessageRequest request) {
        User sender   = userService.findOrThrow(request.getSenderId());
        User receiver = userService.findOrThrow(request.getReceiverId());

        Message saved = messageRepository.save(
                Message.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .message(request.getMessage())
                        .build()
        );

        return toResponse(saved);
    }

    /**
     * Retrieve the full message thread for a match (both directions, ordered by time).
     * Looks up the two users via the match record.
     */
    public List<MessageResponse> getMessagesByMatch(Long matchId) {
        Match match = matchService.findMatchOrThrow(matchId);

        Long userAId = match.getUserA().getId();
        Long userBId = match.getUserB().getId();

        return messageRepository.findConversation(userAId, userBId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private MessageResponse toResponse(Message msg) {
        return MessageResponse.builder()
                .id(msg.getId())
                .sender(userService.toResponse(msg.getSender()))
                .receiver(userService.toResponse(msg.getReceiver()))
                .message(msg.getMessage())
                .timestamp(msg.getTimestamp() != null ? msg.getTimestamp().toString() : null)
                .build();
    }
}
