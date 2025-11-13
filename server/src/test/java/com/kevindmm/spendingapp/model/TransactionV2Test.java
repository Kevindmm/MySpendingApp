package com.kevindmm.spendingapp.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionV2Test {

    @Test
    void setAmount_andGetAmount_worksCorrectly() {
        TransactionV2 transaction = new TransactionV2();
        transaction.setAmount(150.75f);
        assertEquals(150.75f, transaction.getAmount());
    }

    @Test
    void setCurrency_andGetCurrency_worksCorrectly() {
        TransactionV2 transaction = new TransactionV2();
        transaction.setCurrency("USD");
        assertEquals("USD", transaction.getCurrency());
    }

    @Test
    void setDate_andGetDate_worksCorrectly() {
        TransactionV2 transaction = new TransactionV2();
        LocalDate date = LocalDate.of(2024, 1, 15);
        transaction.setDate(date);
        assertEquals(date, transaction.getDate());
    }

    @Test
    void setNote_andGetNote_worksCorrectly() {
        TransactionV2 transaction = new TransactionV2();
        transaction.setNote("Test note");
        assertEquals("Test note", transaction.getNote());
    }

    @Test
    void setUser_andGetUser_worksCorrectly() {
        TransactionV2 transaction = new TransactionV2();
        User user = new User();
        user.setEmail("test@example.com");
        transaction.setUser(user);
        assertEquals(user, transaction.getUser());
        assertEquals("test@example.com", transaction.getUser().getEmail());
    }

    @Test
    void setCategory_andGetCategory_worksCorrectly() {
        TransactionV2 transaction = new TransactionV2();
        Category category = new Category();
        category.setName("Food");
        transaction.setCategory(category);
        assertEquals(category, transaction.getCategory());
        assertEquals("Food", transaction.getCategory().getName());
    }

    @Test
    void getId_returnsNullBeforePersistence() {
        TransactionV2 transaction = new TransactionV2();
        assertNull(transaction.getId());
    }

    @Test
    void getCreatedAt_returnsNullBeforePersistence() {
        TransactionV2 transaction = new TransactionV2();
        assertNull(transaction.getCreatedAt());
    }

    @Test
    void getUpdatedAt_returnsNullBeforePersistence() {
        TransactionV2 transaction = new TransactionV2();
        assertNull(transaction.getUpdatedAt());
    }

    @Test
    void constructor_withParameters_setsFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        Float amount = 100.50f;
        String currency = "EUR";
        LocalDate date = LocalDate.of(2024, 3, 20);

        TransactionV2 transaction = new TransactionV2(id, amount, currency, date);

        assertEquals(id, transaction.getId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(currency, transaction.getCurrency());
        assertEquals(date, transaction.getDate());
    }
}
