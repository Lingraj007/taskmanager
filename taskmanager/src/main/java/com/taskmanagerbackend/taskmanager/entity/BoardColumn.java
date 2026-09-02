package com.taskmanagerbackend.taskmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "board_columns")
@Data
public class BoardColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    private String name;

    private  Integer position;
}
