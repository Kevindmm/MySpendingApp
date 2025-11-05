package com.kevindmm.spendingapp.repository;

import com.kevindmm.spendingapp.model.Category;
import com.kevindmm.spendingapp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest
@Sql("/data-h2.sql")
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private UserRepository userRepo;

    @Test
    void saveAndFindCategoryById() {
        // Arrange
        Optional<User> adminUser = userRepo.findByEmail("admin@example.com");
        assertTrue(adminUser.isPresent());

        Category category = new Category();
        category.setName("Entertainment");
        category.setColor("#FF00FF");
        category.setUser(adminUser.get());

        // Act
        Category savedCategory = categoryRepo.save(category);
        Optional<Category> foundCategory = categoryRepo.findById(savedCategory.getId());

        // Assert
        assertTrue(foundCategory.isPresent());
        assertEquals("Entertainment", foundCategory.get().getName());
        assertEquals("#FF00FF", foundCategory.get().getColor());
        assertEquals(adminUser.get().getId(), foundCategory.get().getUser().getId());
    }

    @Test
    void findCategoriesByUserId_returnsUserCategories() {
        // Arrange
        Optional<User> adminUser = userRepo.findByEmail("admin@example.com");
        assertTrue(adminUser.isPresent());

        // Act
        List<Category> adminCategories = categoryRepo.findByUserId(adminUser.get().getId());

        // Assert
        assertEquals(3, adminCategories.size()); // Food, Health, Transport from data-h2.sql
        assertTrue(adminCategories.stream().anyMatch(cat -> cat.getName().equals("Food")));
        assertTrue(adminCategories.stream().anyMatch(cat -> cat.getName().equals("Health")));
        assertTrue(adminCategories.stream().anyMatch(cat -> cat.getName().equals("Transport")));
    }

    @Test
    void findCategoriesByUserId_whenUserHasNoCategories_returnsEmptyList() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setName("New");
        newUser.setPasswordHash("hash");
        User savedUser = userRepo.save(newUser);

        // Act
        List<Category> categories = categoryRepo.findByUserId(savedUser.getId());

        // Assert
        assertTrue(categories.isEmpty());
    }

    @Test
    void updateCategory_successfullyUpdatesName() {
        // Arrange
        Optional<User> adminUser = userRepo.findByEmail("admin@example.com");
        assertTrue(adminUser.isPresent());

        List<Category> categories = categoryRepo.findByUserId(adminUser.get().getId());
        Category foodCategory = categories.stream()
                .filter(cat -> cat.getName().equals("Food"))
                .findFirst()
                .orElseThrow();

        String originalName = foodCategory.getName();

        // Act
        foodCategory.setName("Groceries");
        categoryRepo.save(foodCategory);

        // Assert
        Optional<Category> updatedCategory = categoryRepo.findById(foodCategory.getId());
        assertTrue(updatedCategory.isPresent());
        assertEquals("Groceries", updatedCategory.get().getName());
        assertNotEquals(originalName, updatedCategory.get().getName());
    }

    @Test
    void deleteCategory_removesCategoryFromDatabase() {
        // Arrange
        Optional<User> johnUser = userRepo.findByEmail("john.doe@example.com");
        assertTrue(johnUser.isPresent());

        List<Category> johnCategories = categoryRepo.findByUserId(johnUser.get().getId());
        assertFalse(johnCategories.isEmpty());

        UUID categoryId = johnCategories.get(0).getId();

        // Act
        categoryRepo.deleteById(categoryId);

        // Assert
        Optional<Category> deletedCategory = categoryRepo.findById(categoryId);
        assertTrue(deletedCategory.isEmpty());
    }

    @Test
    void countCategories_returnsCorrectNumber() {
        // Act
        long categoryCount = categoryRepo.count();

        // Assert
        assertEquals(4, categoryCount); // 3 for admin + 1 for john.doe from data-h2.sql
    }

    @Test
    void saveCategory_withNullUser_throwsException() {
        // Arrange
        Category category = new Category();
        category.setName("Invalid");
        category.setColor("#000000");
        category.setUser(null); // Violates NOT NULL constraint

        // Act & Assert
        assertThrows(Exception.class, () -> {
            categoryRepo.saveAndFlush(category);
        });
    }

    @Test
    void findByNameAndUserId_returnsCorrectCategory() {
        // Arrange
        Optional<User> adminUser = userRepo.findByEmail("admin@example.com");
        assertTrue(adminUser.isPresent());

        // Act
        Optional<Category> foodCategory = categoryRepo.findByNameAndUserId("Food", adminUser.get().getId());

        // Assert
        assertTrue(foodCategory.isPresent());
        assertEquals("Food", foodCategory.get().getName());
        assertEquals("#FF5733", foodCategory.get().getColor());
    }

    @Test
    void findByNameAndUserId_whenCategoryDoesNotExist_returnsEmpty() {
        // Arrange
        Optional<User> adminUser = userRepo.findByEmail("admin@example.com");
        assertTrue(adminUser.isPresent());

        // Act
        Optional<Category> nonExistentCategory = categoryRepo.findByNameAndUserId("NonExistent", adminUser.get().getId());

        // Assert
        assertTrue(nonExistentCategory.isEmpty());
    }
}
