package com.kevindmm.spendingapp.repository;

import com.kevindmm.spendingapp.model.User;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Sql("/data-h2.sql")
public class UserRepositoryTest {

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
    void saveAndFindUserById() {

        //Arrange
        User user = new User();
        user.setEmail("test@eg.com");
        user.setName("Pepito");
        user.setPasswordHash("hashedpassword");

        //Act
        User savedUser = userRepo.save(user);
        Optional<User> foundUser = userRepo.findById(savedUser.getId());

        //Assert
        assertTrue(foundUser.isPresent());
        assertEquals("test@eg.com", foundUser.get().getEmail());
        assertEquals("Pepito", foundUser.get().getName());
        assertEquals("hashedpassword", foundUser.get().getPasswordHash());

    }

    @Test
    void findAnExistingUserByEmail() {

        //Act
        Optional<User> existingUser = userRepo.findByEmail("john.doe@example.com");

        //Assert
        assertTrue(existingUser.isPresent());
        assertEquals("John", existingUser.get().getName());
        assertEquals("john.doe@example.com", existingUser.get().getEmail());
        assertEquals("$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1VCYnlhBqT1r8p6cDK8Zm2L8d0YFe1u", existingUser.get().getPasswordHash());
    }

    @Test
    void findNonExistingUserByEmail() {

        //Act
        Optional<User> nonExistingUser = userRepo.findByEmail("nonexistingmail@eg.com");

        //Assert
        assertTrue(nonExistingUser.isEmpty());
    }

    @Test
    void saveUser_withDuplicateEmail_throwsException() {

        //Arrange
        User user = new User();
        user.setEmail("existingEmail@example.com");
        user.setName("Alice");
        user.setPasswordHash("hashedpassword");


        User duplicateUser = new User();
        duplicateUser.setEmail("existingEmail@example.com");
        duplicateUser.setName("Bob");
        duplicateUser.setPasswordHash("anotherhashedpassword");

        //Act & Assert
        userRepo.save(user);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepo.saveAndFlush(duplicateUser); // Use saveAndFlush to force immediate constraint check
        });
    }

    @Test
    void updateUserEmail_successfullyUpdatesEmail() {

        //Arrange
        Optional<User> existingUser = userRepo.findByEmail("john.doe@example.com");
        assertTrue(existingUser.isPresent());

        User userToUpdate = existingUser.get();
        String originalEmail = userToUpdate.getEmail();
        String newEmail = "jd@eg.com";

        //Act
        userToUpdate.setEmail(newEmail);
        userRepo.save(userToUpdate);

        //Assert
        Optional<User> updatedUser = userRepo.findById(userToUpdate.getId());
        assertTrue(updatedUser.isPresent());
        assertNotEquals(originalEmail, updatedUser.get().getEmail());
        assertEquals(newEmail, updatedUser.get().getEmail());
    }

    @Test
    void deleteUser_removesUserFromDatabase() {

        //Arrange
        Optional<User> existingUser = userRepo.findByEmail("john.doe@example.com");
        assertTrue(existingUser.isPresent());

        UUID userId = existingUser.get().getId();

        //Act
        userRepo.deleteById(userId);

        //Assert
        Optional<User> deletedUser = userRepo.findById(userId);
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    void countUsers_returnsCorrectNumber() {

        //Act
        long userCount = userRepo.count();

        //Assert
        assertEquals(2, userCount); //data-h2.sql inserts 2 users (admin and john.doe)
    }

    @Test
    void saveUser_withNullName_throwsException() {

        //Arrange
        User user = new User();
        user.setName("test");
        user.setPasswordHash("hash");
        user.setEmail(null); // Email is null, which should violate NOT NULL constraint

        //Act & Assert
        assertThrows(Exception.class, () -> {
            userRepo.saveAndFlush(user);
        });
    }
}