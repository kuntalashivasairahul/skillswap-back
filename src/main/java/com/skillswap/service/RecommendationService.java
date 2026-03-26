package com.skillswap.service;

import com.skillswap.dto.AIRecommendationResponse;
import com.skillswap.model.Skill;
import com.skillswap.model.UserOfferedSkill;
import com.skillswap.repository.SkillRepository;
import com.skillswap.repository.UserNeededSkillRepository;
import com.skillswap.repository.UserOfferedSkillRepository;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Provides simple skill recommendations for users.
 *
 * Current placeholder logic:
 * Recommend skills that appear in other users' offered skills but are not already
 * in the current user's offered or needed skill sets.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final SkillRepository skillRepository;
    private final UserOfferedSkillRepository offeredSkillRepository;
    private final UserNeededSkillRepository neededSkillRepository;
    private final UserRepository userRepository;

    /**
     * Build skill recommendations for the given user.
     */
    public AIRecommendationResponse recommendSkills(Long userId) {
        // Validate user exists before computing recommendations
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Current user's existing skill IDs
        Set<Long> userOffered = offeredSkillRepository.findSkillIdsByUserId(userId);
        Set<Long> userNeeded = neededSkillRepository.findSkillIdsByUserId(userId);

        // Exclude skills the user already offers or needs
        Set<Long> exclusionSet = new HashSet<>(userOffered);
        exclusionSet.addAll(userNeeded);

        // Gather offered-skill rows from users other than current user
        List<UserOfferedSkill> otherUsersOfferedSkills = userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(userId))
                .flatMap(user -> offeredSkillRepository.findByUserId(user.getId()).stream())
                .collect(Collectors.toList());

        // Group by skillId to compute ranking metrics
        Map<Long, List<UserOfferedSkill>> offeredBySkill = otherUsersOfferedSkills.stream()
                .filter(record -> record.getSkill() != null && record.getSkill().getId() != null)
                .collect(Collectors.groupingBy(record -> record.getSkill().getId()));

        // Resolve skill IDs to names
        Map<Long, Skill> skillsById = skillRepository.findAllById(offeredBySkill.keySet()).stream()
                .collect(Collectors.toMap(Skill::getId, Function.identity()));

        List<String> recommendedSkillNames = offeredBySkill.entrySet().stream()
                // Remove skills current user already has in offered/needed lists
                .filter(entry -> !exclusionSet.contains(entry.getKey()))
                .map(entry -> {
                    Long skillId = entry.getKey();
                    List<UserOfferedSkill> records = entry.getValue();

                    // Popularity = number of distinct users offering this skill
                    long popularity = records.stream()
                            .map(record -> record.getUser().getId())
                            .distinct()
                            .count();

                    // Avg rating = average rating of users offering this skill
                    double avgRating = records.stream()
                            .map(UserOfferedSkill::getUser)
                            .map(user -> user.getRating() != null ? user.getRating() : 0.0)
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);

                    double score = popularity + avgRating;
                    Skill skill = skillsById.get(skillId);
                    String skillName = (skill != null) ? skill.getName() : null;

                    return new AbstractMap.SimpleEntry<>(skillName, score);
                })
                .filter(entry -> entry.getKey() != null)
                // Sort by score descending, then by skill name for deterministic order
                .sorted(
                        Comparator.<AbstractMap.SimpleEntry<String, Double>>comparingDouble(AbstractMap.SimpleEntry::getValue)
                                .reversed()
                                .thenComparing(AbstractMap.SimpleEntry::getKey)
                )
                .limit(5)
                .map(AbstractMap.SimpleEntry::getKey)
                .collect(Collectors.toList());

        return AIRecommendationResponse.builder()
                .recommendedSkills(recommendedSkillNames)
                .build();
    }
}
