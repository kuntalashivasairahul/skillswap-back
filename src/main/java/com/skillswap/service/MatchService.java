package com.skillswap.service;

import com.skillswap.dto.MatchResponse;
import com.skillswap.model.Match;
import com.skillswap.model.MatchStatus;
import com.skillswap.model.User;
import com.skillswap.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core matchmaking engine.
 *
 * A valid match exists between userA and userB when:
 *   userA.offeredSkills ∩ userB.neededSkills ≠ ∅
 *   AND
 *   userB.offeredSkills ∩ userA.neededSkills ≠ ∅
 *
 * Match score formula:
 *   matchScore = (|intersection of mutually matched skills| × 2) + candidateUser.rating
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserRepository             userRepository;
    private final MatchRepository            matchRepository;
    private final UserOfferedSkillRepository offeredRepo;
    private final UserNeededSkillRepository  neededRepo;
    private final UserService                userService;

    // ── Matchmaking algorithm ──────────────────────────────────────────────────

    /**
     * Find all users who are a valid bidirectional match for the given user,
     * sorted by descending match score.
     *
     * @param userId the ID of the user looking for matches
     * @return list of match responses ordered best-first
     */
    public List<MatchResponse> findMatches(Long userId) {
        // Step 1: gather the requesting user's skill sets
        User self = userService.findOrThrow(userId);
        Set<Long> myOffered = offeredRepo.findSkillIdsByUserId(userId);
        Set<Long> myNeeded  = neededRepo.findSkillIdsByUserId(userId);

        log.debug("findMatches(userId={}) offeredSkills={} neededSkills={}", userId, myOffered, myNeeded);

        if (myOffered.isEmpty() || myNeeded.isEmpty()) {
            return Collections.emptyList();  // Can't match if skill sets are empty
        }

        // Step 2: iterate all other users and test bidirectional overlap
        List<User> candidates = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(userId))
                .collect(Collectors.toList());

        List<MatchResponse> results = new ArrayList<>();

        for (User candidate : candidates) {
            Set<Long> theirOffered = offeredRepo.findSkillIdsByUserId(candidate.getId());
            Set<Long> theirNeeded  = neededRepo.findSkillIdsByUserId(candidate.getId());

            // Skills I teach that they want to learn
            Set<Long> iTeachThemIntersection = intersection(myOffered, theirNeeded);
            // Skills they teach that I want to learn
            Set<Long> theyTeachMeIntersection = intersection(theirOffered, myNeeded);

            // Both sides of the exchange must be non-empty for a valid match
            if (!iTeachThemIntersection.isEmpty() && !theyTeachMeIntersection.isEmpty()) {
                int sharedSkills = countSharedSkills(iTeachThemIntersection, theyTeachMeIntersection);
                double score = calculateMatchScore(sharedSkills, candidate.getRating());

                log.debug("  Candidate {} qualifies with score {}", candidate.getId(), score);

                // Build a synthetic (not yet persisted) MatchResponse for the UI
                results.add(MatchResponse.builder()
                        .id(null)  // No DB record yet — user must call /request to persist
                    .userA(userService.toResponse(self))
                        .userB(userService.toResponse(candidate))
                        .status(MatchStatus.PENDING.name())
                        .matchScore(score)
                        .createdAt(null)
                        .build());
            }
        }

            // Rank by score descending, then candidate rating descending for deterministic ties.
            results.sort(
                Comparator.comparingDouble(MatchResponse::getMatchScore)
                    .thenComparingDouble(m -> m.getUserB() != null && m.getUserB().getRating() != null
                        ? m.getUserB().getRating()
                        : 0.0)
                    .reversed()
            );
        return results;
    }

    // ── Match request CRUD ────────────────────────────────────────────────────

    /**
     * Persist a match request (status = PENDING).
     *
     * @throws IllegalArgumentException if the match pair already exists
     */
    @Transactional
    public MatchResponse requestMatch(Long requesterId, Long recipientId) {
        if (matchRepository.existsByUserPair(requesterId, recipientId)) {
            throw new IllegalArgumentException("Match request already exists between these users");
        }

        User requester = userService.findOrThrow(requesterId);
        User recipient = userService.findOrThrow(recipientId);

        // Compute score at request time
        Set<Long> myOffered    = offeredRepo.findSkillIdsByUserId(requesterId);
        Set<Long> myNeeded     = neededRepo.findSkillIdsByUserId(requesterId);
        Set<Long> theirOffered = offeredRepo.findSkillIdsByUserId(recipientId);
        Set<Long> theirNeeded  = neededRepo.findSkillIdsByUserId(recipientId);

        int sharedSkills = countSharedSkills(intersection(myOffered, theirNeeded), intersection(theirOffered, myNeeded));
        double score = calculateMatchScore(sharedSkills, recipient.getRating());

        Match match = matchRepository.save(
                Match.builder()
                        .userA(requester)
                        .userB(recipient)
                        .status(MatchStatus.PENDING)
                        .matchScore(score)
                        .build()
        );

        return toResponse(match);
    }

    /** Accept a pending match request, updating its status to ACCEPTED. */
    @Transactional
    public MatchResponse acceptMatch(Long matchId) {
        Match match = findMatchOrThrow(matchId);
        match.setStatus(MatchStatus.ACCEPTED);
        return toResponse(matchRepository.save(match));
    }

    /** Reject a pending match request. */
    @Transactional
    public MatchResponse rejectMatch(Long matchId) {
        Match match = findMatchOrThrow(matchId);
        match.setStatus(MatchStatus.REJECTED);
        return toResponse(matchRepository.save(match));
    }

    /** Return all match records (any status) for a given user. */
    public List<MatchResponse> getMatchesForUser(Long userId) {
        return matchRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Package-accessible helper ─────────────────────────────────────────────

    public Match findMatchOrThrow(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + id));
    }

    public MatchResponse toResponse(Match match) {
        return MatchResponse.builder()
                .id(match.getId())
                .userA(userService.toResponse(match.getUserA()))
                .userB(userService.toResponse(match.getUserB()))
                .status(match.getStatus().name())
                .matchScore(match.getMatchScore())
                .createdAt(match.getCreatedAt() != null ? match.getCreatedAt().toString() : null)
                .build();
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    /** Returns a new Set containing elements present in both a and b. */
    private static Set<Long> intersection(Set<Long> a, Set<Long> b) {
        Set<Long> result = new HashSet<>(a);
        result.retainAll(b);
        return result;
    }

    /**
     * Counts total shared skills that make the exchange mutually valuable.
     *
     * sharedSkills =
     *   count(requesterOffered ∩ candidateNeeded)
     * + count(candidateOffered ∩ requesterNeeded)
     */
    private static int countSharedSkills(Set<Long> requesterToCandidate, Set<Long> candidateToRequester) {
        return requesterToCandidate.size() + candidateToRequester.size();
    }

    /**
     * Match scoring function used for ranking recommendations and persisting requests.
     *
     * Formula:
     *   matchScore = (sharedSkills * 2) + candidateRating
     */
    private static double calculateMatchScore(int sharedSkills, Double candidateRating) {
        double safeRating = candidateRating != null ? candidateRating : 0.0;
        return (sharedSkills * 2.0) + safeRating;
    }
}
