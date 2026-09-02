package com.taskmanagerbackend.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardColumnResponse {
    private Long id;
    private String name;
    private int position;
}