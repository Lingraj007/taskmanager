package com.taskmanagerbackend.taskmanager.entity;

import jakarta.annotation.Priority;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "tasks")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "column_id")
    private BoardColumn column;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private LocalDate dueDate;

    private  Integer position;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private  User createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}
