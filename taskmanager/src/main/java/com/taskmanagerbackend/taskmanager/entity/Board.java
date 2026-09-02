package com.taskmanagerbackend.taskmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "boards")
@Data
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private String name;

}
