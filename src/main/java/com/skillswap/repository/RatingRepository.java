package com.skillswap.repository;

import com.skillswap.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access layer for {@link Rating} records.
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    /** All ratings received by a specific user. */
    List<Rating> findByRatedUserId(Long ratedUserId);

    /**
     * Calculate the current average rating for a user — used to refresh
     * the denormalised {@code rating} field on the User entity.
     */
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Rating r WHERE r.ratedUser.id = :userId")
    Double calculateAverageRating(@Param("userId") Long userId);
}
