package com.kevindmm.spendingapp.repository;

import java.util.List;

import com.kevindmm.spendingapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "SELECT * FROM transactions WHERE date >= ?1 AND date <= ?2", nativeQuery = true)
    List<Transaction> getTransactionsForRange(String startDate, String endDate);
}
