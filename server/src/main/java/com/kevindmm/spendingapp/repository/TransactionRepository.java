package com.kevindmm.spendingapp.repository;

import java.util.List;
import java.util.UUID;

import com.kevindmm.spendingapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query(value = "SELECT * FROM transactions WHERE date >= ?1 AND date <= ?2", nativeQuery = true)
    List<Transaction> getTransactionsForRange(String startDate, String endDate);

    //Using triple quotes for readability in Java17
    @Query("""
        SELECT t FROM Transaction t
        JOIN t.user u
        WHERE u.email = ?1
        """)
    List<Transaction> findByEmail(String email);

    List<Transaction> findByUserId(UUID userId);
    List<Transaction> findByUserIdAndCategoryId(UUID userId, UUID categoryId);
}
