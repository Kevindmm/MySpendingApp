package com.kevindmm.spendingapp.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(name = "transactions")
public class TransactionV2 {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    //P1.7 add `note` field to Transaction entity for optional free-text details
    @Column(name = "note", length = 255)
    private String note;

    //P1.5 User and Category relationships
    @ManyToOne(fetch = FetchType.LAZY) //FK to User
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) //FK to Category
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public TransactionV2(){}

    public TransactionV2(UUID id, Float amount, String currency, LocalDate transactionDate){
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.date = transactionDate;
    }

    public UUID getId(){
        return id;
    }

    public Timestamp getCreatedAt(){
        return createdAt;
    }

    public Timestamp getUpdatedAt(){
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

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
}
