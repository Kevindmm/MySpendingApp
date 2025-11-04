package com.kevindmm.spendingapp.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_name",
                columnNames = {"user_id", "name"} //Unique per user
        )
)
public class Category {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(
            columnDefinition = "CHAR(36)",
            nullable = false, updatable = false
    )
    private UUID id;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(name = "color", length = 7) //e.g., #RRGGBB
    private String color;

    @ManyToOne(fetch = FetchType.LAZY) //FK to User
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
