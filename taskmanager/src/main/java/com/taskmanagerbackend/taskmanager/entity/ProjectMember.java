package com.taskmanagerbackend.taskmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "project_members")
@Data
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private User.Role roleInProject;
}