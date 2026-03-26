package com.skillswap.service;

import com.skillswap.dto.SessionRequest;
import com.skillswap.dto.SessionResponse;
import com.skillswap.model.Match;
import com.skillswap.model.MatchStatus;
import com.skillswap.model.Session;
import com.skillswap.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for booking and managing skill-exchange sessions.
 * Sessions can only be created once both users have ACCEPTED the match.
 */
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final MatchService      matchService;

    /**
     * Book a new session for an ACCEPTED match.
     *
     * @throws IllegalStateException if the match is not in ACCEPTED status
     */
    @Transactional
    public SessionResponse bookSession(SessionRequest request) {
        Match match = matchService.findMatchOrThrow(request.getMatchId());

        if (match.getStatus() != MatchStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Sessions can only be booked for ACCEPTED matches. Current status: " + match.getStatus());
        }

        Session session = sessionRepository.save(
                Session.builder()
                        .match(match)
                        .sessionDate(request.getSessionDate())
                        .build()
        );

        return toResponse(session);
    }

    /** Return all sessions (any status) where the given user is a participant. */
    public List<SessionResponse> getSessionsForUser(Long userId) {
        return sessionRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private SessionResponse toResponse(Session session) {
        return SessionResponse.builder()
                .id(session.getId())
                .match(matchService.toResponse(session.getMatch()))
                .sessionDate(session.getSessionDate().toString())
                .status(session.getStatus().name())
                .build();
    }
}
