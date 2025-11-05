package com.kevindmm.spendingapp.repository;

import com.kevindmm.spendingapp.model.Category;
import com.kevindmm.spendingapp.model.TransactionV2;
import com.kevindmm.spendingapp.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@Sql("/data-h2.sql")
@DataJpaTest
public class TransactionRepositoryV2Test {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private TransactionRepositoryV2 transactionRepo;

    @BeforeAll
    static void before(@Autowired Environment env) {
        System.out.println("activeProfiles=" + Arrays.toString(env.getActiveProfiles()));
        System.out.println("datasource.url=" + env.getProperty("spring.datasource.url"));
    }

    @Test
    void saveAndFindTransactionById() {
        // Arrange
        Optional<User> adminUser = userRepo.findByEmail("admin@example.com");
        assertTrue(adminUser.isPresent());

        List<Category> categories = categoryRepo.findByUserId(adminUser.get().getId());
        Category foodCategory = categories.stream()
                .filter(cat -> cat.getName().equals("Food"))
                .findFirst()
                .orElseThrow();

        TransactionV2 transaction = new TransactionV2();
        transaction.setAmount(75.50f);
        transaction.setCurrency("USD");
        transaction.setDate(LocalDate.now());
        transaction.setNote("Coffee shop");
        transaction.setUser(adminUser.get());
        transaction.setCategory(foodCategory);

        // Act
        TransactionV2 savedTransaction = transactionRepo.save(transaction);
        Optional<TransactionV2> foundTransaction = transactionRepo.findById(savedTransaction.getId());

        // Assert
        assertTrue(foundTransaction.isPresent());
        assertEquals(75.50f, foundTransaction.get().getAmount());
        assertEquals("USD", foundTransaction.get().getCurrency());
        assertEquals("Coffee shop", foundTransaction.get().getNote());
        assertEquals(adminUser.get().getId(), foundTransaction.get().getUser().getId());
        assertEquals(foodCategory.getId(), foundTransaction.get().getCategory().getId());
    }

    @Test
    void findByUserId_returnsUserTransactions() {
        // Arrange
        Optional<User> adminUser = userRepo.findByEmail("admin@example.com");
        assertTrue(adminUser.isPresent());

        // Act
        List<TransactionV2> adminTransactions = transactionRepo.findByUserId(adminUser.get().getId());

        // Assert
        assertEquals(3, adminTransactions.size()); // 3 transactions from data-h2.sql
        assertTrue(adminTransactions.stream().anyMatch(tx -> tx.getNote().equals("Grocery shopping")));
        assertTrue(adminTransactions.stream().anyMatch(tx -> tx.getNote().equals("Monthly gym")));
        assertTrue(adminTransactions.stream().anyMatch(tx -> tx.getNote().equals("Team dinner")));
    }

    @Test
    void findByUserId_whenUserHasNoTransactions_returnsEmptyList() {
        // Arrange
        User newUser = new User();
        newUser.setEmail("newuser@example.com");
        newUser.setName("New");
        newUser.setPasswordHash("hash");
        User savedUser = userRepo.save(newUser);

        // Act
        List<TransactionV2> transactions = transactionRepo.findByUserId(savedUser.getId());

        // Assert
        assertTrue(transactions.isEmpty());
    }

    @Test
    void findByCategoryId_returnsTransactionsForCategory() {
        // Arrange
        List<Category> adminCategories = categoryRepo.findByUserId(
                userRepo.findByEmail("admin@example.com").get().getId()
        );
        Category foodCategory = adminCategories.stream()
                .filter(cat -> cat.getName().equals("Food"))
                .findFirst()
                .orElseThrow();

        // Act
        List<TransactionV2> foodTransactions = transactionRepo.findByCategoryId(foodCategory.getId());

        // Assert
        assertEquals(2, foodTransactions.size()); // "Grocery shopping" and "Team dinner"
        assertTrue(foodTransactions.stream().allMatch(tx -> tx.getCategory().getId().equals(foodCategory.getId())));
    }

    @Test
    void findByUserIdAndDateBetween_returnsTransactionsInRange() {
        // Arrange
        Optional<User> adminUser = userRepo.findByEmail("admin@example.com");
        assertTrue(adminUser.isPresent());

        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();

        // Act
        List<TransactionV2> transactionsInRange = transactionRepo.findByUserIdAndDateBetween(
                adminUser.get().getId(),
                startDate,
                endDate
        );

        // Assert
        assertEquals(3, transactionsInRange.size());
        assertTrue(transactionsInRange.stream().allMatch(tx ->
                !tx.getDate().isBefore(startDate) && !tx.getDate().isAfter(endDate)
        ));
    }

    @Test
    void updateTransaction_successfullyUpdatesAmount() {
        // Arrange
        Optional<User> adminUser = userRepo.findByEmail("admin@example.com");
        assertTrue(adminUser.isPresent());

        List<TransactionV2> transactions = transactionRepo.findByUserId(adminUser.get().getId());
        TransactionV2 transaction = transactions.stream()
                .filter(tx -> tx.getNote().equals("Grocery shopping"))
                .findFirst()
                .orElseThrow();

        Float originalAmount = transaction.getAmount();
        Float newAmount = 99.99f;

        // Act
        transaction.setAmount(newAmount);
        transactionRepo.save(transaction);

        // Assert
        Optional<TransactionV2> updatedTransaction = transactionRepo.findById(transaction.getId());
        assertTrue(updatedTransaction.isPresent());
        assertEquals(newAmount, updatedTransaction.get().getAmount());
        assertNotEquals(originalAmount, updatedTransaction.get().getAmount());
    }

    @Test
    void deleteTransaction_removesTransactionFromDatabase() {
        // Arrange
        Optional<User> johnUser = userRepo.findByEmail("john.doe@example.com");
        assertTrue(johnUser.isPresent());

        List<TransactionV2> johnTransactions = transactionRepo.findByUserId(johnUser.get().getId());
        assertFalse(johnTransactions.isEmpty());

        UUID transactionId = johnTransactions.get(0).getId();

        // Act
        transactionRepo.deleteById(transactionId);

        // Assert
        Optional<TransactionV2> deletedTransaction = transactionRepo.findById(transactionId);
        assertTrue(deletedTransaction.isEmpty());
    }

    @Test
    void countTransactions_returnsCorrectNumber() {
        // Act
        long transactionCount = transactionRepo.count();

        // Assert
        assertEquals(4, transactionCount); // 4 transactions from data-h2.sql
    }

    @Test
    void saveTransaction_withNullUser_throwsException() {
        // Arrange
        TransactionV2 transaction = new TransactionV2();
        transaction.setAmount(100.00f);
        transaction.setCurrency("USD");
        transaction.setDate(LocalDate.now());
        transaction.setUser(null); // Violates NOT NULL constraint

        // Act & Assert
        assertThrows(Exception.class, () -> {
            transactionRepo.saveAndFlush(transaction);
        });
    }

    @Test
    void saveTransaction_withNullCategory_throwsException() {
        // Arrange
        Optional<User> adminUser = userRepo.findByEmail("admin@example.com");
        assertTrue(adminUser.isPresent());

        TransactionV2 transaction = new TransactionV2();
        transaction.setAmount(100.00f);
        transaction.setCurrency("USD");
        transaction.setDate(LocalDate.now());
        transaction.setUser(adminUser.get());
        transaction.setCategory(null); // Violates NOT NULL constraint

        // Act & Assert
        assertThrows(Exception.class, () -> {
            transactionRepo.saveAndFlush(transaction);
        });
    }

}
