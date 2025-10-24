package com.kevindmm.spendingapp.repository;

import com.kevindmm.spendingapp.model.Category;
import com.kevindmm.spendingapp.model.User;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest
public class UserCategoryRepoTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CategoryRepository catRepo;

    @BeforeAll
    static void before(@Autowired Environment env) {
        System.out.println("activeProfiles=" + Arrays.toString(env.getActiveProfiles()));
        System.out.println("datasource.url=" + env.getProperty("spring.datasource.url"));
    }

    @Test
    void saveAndFindUserAndCategory() {
        // 1) Save a user
        User u = new User();
        u.setEmail("t@t.com");
        u.setPasswordHash("hash");
        u = userRepo.save(u);

        // 2) Save a category for that user
        Category c = new Category();
        c.setName("TestCat");
        c.setUser(u);
        c = catRepo.save(c);

        // 3) Get a user-category and check its data
        Optional<User> maybe = userRepo.findById(u.getId());
        assertTrue(maybe.isPresent());
        assertEquals("t@t.com", maybe.get().getEmail());

        List<Category> cats = catRepo.findByUserId(u.getId());
        assertEquals(1, cats.size());
        assertEquals("TestCat", cats.get(0).getName());
    }
}
