package com.skillswap.config;

import com.skillswap.model.Skill;
import com.skillswap.model.User;
import com.skillswap.model.UserNeededSkill;
import com.skillswap.model.UserOfferedSkill;
import com.skillswap.repository.SkillRepository;
import com.skillswap.repository.UserNeededSkillRepository;
import com.skillswap.repository.UserOfferedSkillRepository;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Seeds default reference data on application startup.
 *
 * This initializer is idempotent: each skill is inserted only if it does not
 * already exist in the database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String DEMO_PASSWORD = "password123";

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserOfferedSkillRepository offeredSkillRepository;
    private final UserNeededSkillRepository neededSkillRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        List<Skill> defaultSkills = List.of(
                Skill.builder().name("Java").description("Object-oriented programming with Java").category("Programming").build(),
                Skill.builder().name("Python").description("General-purpose programming and scripting").category("Programming").build(),
                Skill.builder().name("Photoshop").description("Image editing and graphic design using Adobe Photoshop").category("Design").build(),
                Skill.builder().name("UI Design").description("Designing intuitive and visually appealing user interfaces").category("Design").build(),
                Skill.builder().name("Data Science").description("Data analysis, statistics, and machine learning fundamentals").category("Data").build()
        );

        int inserted = 0;
        for (Skill skill : defaultSkills) {
            if (!skillRepository.existsByName(skill.getName())) {
                skillRepository.save(skill);
                inserted++;
            }
        }

        Map<String, Skill> skillsByName = loadSkillsByName(defaultSkills);
        int demoUsersInserted = seedDemoUsers(skillsByName);

        log.info("Default seed completed. Inserted {} new skill(s) and {} demo user(s).", inserted, demoUsersInserted);
        logDemoCredentials();
    }

    private Map<String, Skill> loadSkillsByName(List<Skill> defaultSkills) {
        Map<String, Skill> skillsByName = new HashMap<>();
        for (Skill skill : defaultSkills) {
            Skill persisted = skillRepository.findByName(skill.getName())
                    .orElseThrow(() -> new IllegalStateException("Expected seeded skill not found: " + skill.getName()));
            skillsByName.put(skill.getName(), persisted);
        }
        return skillsByName;
    }

    private int seedDemoUsers(Map<String, Skill> skillsByName) {
        List<DemoUserSeed> demoUsers = List.of(
                new DemoUserSeed("Alice Johnson", "alice@skillswap.dev", "Frontend enthusiast who teaches Java and wants to improve product design.", 4.8, "Java", "UI Design"),
                new DemoUserSeed("Brian Lee", "brian@skillswap.dev", "UI designer looking to strengthen backend engineering skills.", 4.5, "UI Design", "Java"),
                new DemoUserSeed("Carla Mendes", "carla@skillswap.dev", "Python mentor who wants to learn visual editing workflows.", 4.7, "Python", "Photoshop"),
                new DemoUserSeed("David Kim", "david@skillswap.dev", "Creative designer interested in automation and scripting.", 4.3, "Photoshop", "Python"),
                new DemoUserSeed("Emma Patel", "emma@skillswap.dev", "Data science student looking to build stronger Java skills.", 4.9, "Data Science", "Java"),
                new DemoUserSeed("Frank Moore", "frank@skillswap.dev", "Java developer exploring analytics and machine learning.", 4.4, "Java", "Data Science")
        );

        int insertedUsers = 0;
        Random random = new Random(42L);
        List<Skill> allSkills = new ArrayList<>(skillsByName.values());

        for (DemoUserSeed seed : demoUsers) {
            boolean userAlreadyExists = userRepository.existsByEmail(seed.email());
            User user = userRepository.findByEmail(seed.email())
                    .orElseGet(() -> userRepository.save(User.builder()
                            .name(seed.name())
                            .email(seed.email())
                            .password(passwordEncoder.encode(DEMO_PASSWORD))
                            .bio(seed.bio())
                            .rating(seed.rating())
                            .build()));

            if (!userAlreadyExists) {
                insertedUsers++;
            }

            if (user.getPassword() == null || user.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(seed.name());
            }
            if (user.getBio() == null || user.getBio().isBlank()) {
                user.setBio(seed.bio());
            }
            if (user.getRating() == null || user.getRating() == 0.0) {
                user.setRating(seed.rating());
            }
            userRepository.save(user);

            assignSkill(user, skillsByName.get(seed.primaryOfferedSkill()), true);
            assignSkill(user, skillsByName.get(seed.primaryNeededSkill()), false);

            // Add one deterministic random extra offered skill and one extra needed skill
            // to make the dataset less uniform while preserving guaranteed core matches.
            Skill extraOffered = pickRandomSkillExcluding(allSkills, random, List.of(seed.primaryOfferedSkill(), seed.primaryNeededSkill()));
            Skill extraNeeded = pickRandomSkillExcluding(allSkills, random, List.of(seed.primaryOfferedSkill(), seed.primaryNeededSkill(), extraOffered.getName()));

            assignSkill(user, extraOffered, true);
            assignSkill(user, extraNeeded, false);
        }

        return insertedUsers;
    }

    private void assignSkill(User user, Skill skill, boolean offered) {
        if (offered) {
            if (!offeredSkillRepository.existsByUserIdAndSkillId(user.getId(), skill.getId())) {
                offeredSkillRepository.save(UserOfferedSkill.builder().user(user).skill(skill).build());
            }
            return;
        }

        if (!neededSkillRepository.existsByUserIdAndSkillId(user.getId(), skill.getId())) {
            neededSkillRepository.save(UserNeededSkill.builder().user(user).skill(skill).build());
        }
    }

    private Skill pickRandomSkillExcluding(List<Skill> allSkills, Random random, List<String> excludedNames) {
        List<Skill> candidates = new ArrayList<>(allSkills);
        candidates.removeIf(skill -> excludedNames.contains(skill.getName()));
        if (candidates.isEmpty()) {
            throw new IllegalStateException("No candidate skills available for demo-user random assignment");
        }
        Collections.shuffle(candidates, random);
        return candidates.get(0);
    }

    private void logDemoCredentials() {
        log.info("========================================");
        log.info("SkillSwap Demo Login Credentials");
        log.info("Password for all demo users: {}", DEMO_PASSWORD);
        log.info("- Alice Johnson  | alice@skillswap.dev");
        log.info("- Brian Lee      | brian@skillswap.dev");
        log.info("- Carla Mendes   | carla@skillswap.dev");
        log.info("- David Kim      | david@skillswap.dev");
        log.info("- Emma Patel     | emma@skillswap.dev");
        log.info("- Frank Moore    | frank@skillswap.dev");
        log.info("========================================");
    }

    private record DemoUserSeed(
            String name,
            String email,
            String bio,
            Double rating,
            String primaryOfferedSkill,
            String primaryNeededSkill
    ) {
    }
}
