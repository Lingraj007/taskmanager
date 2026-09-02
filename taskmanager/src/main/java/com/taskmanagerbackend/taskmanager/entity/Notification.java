package com.taskmanagerbackend.taskmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private  User user;

    private String message;

    private Boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "related_task_id")
    private  Task relatedTask;

    private LocalDateTime createAt = LocalDateTime.now();
}
