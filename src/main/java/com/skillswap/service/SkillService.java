package com.skillswap.service;

import com.skillswap.dto.SkillRequest;
import com.skillswap.dto.SkillResponse;
import com.skillswap.model.Skill;
import com.skillswap.model.User;
import com.skillswap.model.UserNeededSkill;
import com.skillswap.model.UserOfferedSkill;
import com.skillswap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for managing the global skills catalog and per-user skill associations.
 */
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository             skillRepository;
    private final UserRepository              userRepository;
    private final UserOfferedSkillRepository  offeredRepo;
    private final UserNeededSkillRepository   neededRepo;

    // ── Skill catalog ─────────────────────────────────────────────────────────

    /** Create a new skill in the global catalog. */
    @Transactional
    public SkillResponse createSkill(SkillRequest request) {
        if (skillRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Skill already exists: " + request.getName());
        }
        Skill skill = skillRepository.save(
                Skill.builder()
                        .name(request.getName())
                        .description(request.getDescription())
                        .category(request.getCategory())
                        .build()
        );
        return toResponse(skill);
    }

    /** Return all skills in the catalog. */
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Per-user skill associations ───────────────────────────────────────────

    /**
     * Associate a skill with a user as something they can TEACH.
     * Idempotent — silently skips if the association already exists.
     */
    @Transactional
    public void addOfferedSkill(Long userId, Long skillId) {
        if (offeredRepo.existsByUserIdAndSkillId(userId, skillId)) return;

        User  user  = findUserOrThrow(userId);
        Skill skill = findSkillOrThrow(skillId);

        offeredRepo.save(UserOfferedSkill.builder().user(user).skill(skill).build());
    }

    /**
     * Associate a skill with a user as something they want to LEARN.
     * Idempotent — silently skips if the association already exists.
     */
    @Transactional
    public void addNeededSkill(Long userId, Long skillId) {
        if (neededRepo.existsByUserIdAndSkillId(userId, skillId)) return;

        User  user  = findUserOrThrow(userId);
        Skill skill = findSkillOrThrow(skillId);

        neededRepo.save(UserNeededSkill.builder().user(user).skill(skill).build());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    private Skill findSkillOrThrow(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + id));
    }

    private SkillResponse toResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .description(skill.getDescription())
                .category(skill.getCategory())
                .build();
    }
}
