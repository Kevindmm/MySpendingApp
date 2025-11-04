package com.kevindmm.spendingapp.repository;


import com.kevindmm.spendingapp.model.TransactionV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TransactionRepositoryV2 extends JpaRepository<TransactionV2, UUID> {
    //Using triple quotes for readability in Java17
    @Query("""
        SELECT t FROM TransactionV2 t
        JOIN t.user u
        WHERE u.email = ?1
        """)
    List<TransactionV2> findByEmail(String email);

    List<TransactionV2> findByUserId(UUID userId);
    List<TransactionV2> findByUserIdAndCategoryId(UUID userId, UUID categoryId);
}
