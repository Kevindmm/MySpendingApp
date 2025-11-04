package com.kevindmm.spendingapp.model;

import java.sql.Timestamp;
import java.time.LocalDate;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * @deprecated Legacy transaction entity for frontend chart demo.
 * Maps to old 'transactions' table structure (pre-Phase 1).
 * Will be removed when frontend migrates to new Transaction entity with User/Category relationships.
 * See DB-evolution.md § P1.8 for migration plan.
 */
@Deprecated
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt", nullable = false)
    private Timestamp updatedAt;

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public Transaction() {}

    public Transaction(Long id, Float amount, String currency, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

