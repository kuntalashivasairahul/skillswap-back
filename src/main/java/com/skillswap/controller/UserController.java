package com.skillswap.controller;

import com.skillswap.dto.UserResponse;
import com.skillswap.service.SkillService;
import com.skillswap.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User profile endpoints and user-skill association endpoints.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService  userService;
    private final SkillService skillService;

    /** Get a public user profile by ID. */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /** Update mutable profile fields. */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateProfile(id, request.getName(), request.getBio()));
    }

    /** Add an offered skill for a user. */
    @PostMapping("/{id}/offered-skills")
    public ResponseEntity<Void> addOfferedSkill(@PathVariable Long id,
                                                @RequestParam Long skillId) {
        skillService.addOfferedSkill(id, skillId);
        return ResponseEntity.ok().build();
    }

    /** Add a needed skill for a user. */
    @PostMapping("/{id}/needed-skills")
    public ResponseEntity<Void> addNeededSkill(@PathVariable Long id,
                                               @RequestParam Long skillId) {
        skillService.addNeededSkill(id, skillId);
        return ResponseEntity.ok().build();
    }

    @Data
    private static class UpdateUserRequest {
        private String name;
        private String bio;
    }
}
