package com.kevindmm.spendingapp.repository;

import com.kevindmm.spendingapp.model.Category;
import com.kevindmm.spendingapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByUserId(UUID userId);
    Optional<Category> findByNameAndUserId(String name, UUID userId);
}
