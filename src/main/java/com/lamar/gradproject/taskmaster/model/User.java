package com.lamar.gradproject.taskmaster.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // Username

    @Column(nullable = false)
    private String password; // Hashed password

    @Column(nullable = false)
    private String fullname;

    @Column(nullable = false)
    private LocalDateTime registeredOn;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Task> tasks;

    // Getters and Setters
    // (Use Lombok @Data for brevity if preferred)
}
