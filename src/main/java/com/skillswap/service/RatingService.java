package com.skillswap.service;

import com.skillswap.dto.RatingRequest;
import com.skillswap.dto.RatingResponse;
import com.skillswap.model.Rating;
import com.skillswap.model.User;
import com.skillswap.repository.RatingRepository;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for submitting and reading ratings.
 *
 * When a new rating is submitted the rated user's denormalised average
 * rating on the {@code users} table is recalculated automatically.
 */
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository   userRepository;
    private final UserService      userService;

    /**
     * Submit a rating for another user.
     * The rated user's average rating is recomputed and persisted atomically.
     *
     * @throws IllegalArgumentException if a user ID is invalid or rating value is out of range
     */
    @Transactional
    public RatingResponse submitRating(RatingRequest request) {
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        User rater     = userService.findOrThrow(request.getUserId());
        User ratedUser = userService.findOrThrow(request.getRatedUserId());

        Rating rating = ratingRepository.save(
                Rating.builder()
                        .user(rater)
                        .ratedUser(ratedUser)
                        .rating(request.getRating())
                        .feedback(request.getFeedback())
                        .build()
        );

        // Refresh the denormalised average on the User entity
        refreshAverageRating(ratedUser);

        return toResponse(rating);
    }

    /** Return all ratings received by a specific user. */
    public List<RatingResponse> getRatingsForUser(Long userId) {
        return ratingRepository.findByRatedUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Recalculate avg rating from all persisted ratings and update the User row.
     * Called after each new rating submission.
     */
    private void refreshAverageRating(User user) {
        Double avg = ratingRepository.calculateAverageRating(user.getId());
        user.setRating(avg != null ? avg : 0.0);
        userRepository.save(user);
    }

    private RatingResponse toResponse(Rating r) {
        return RatingResponse.builder()
                .id(r.getId())
                .user(userService.toResponse(r.getUser()))
                .ratedUser(userService.toResponse(r.getRatedUser()))
                .rating(r.getRating())
                .feedback(r.getFeedback())
                .build();
    }
}
