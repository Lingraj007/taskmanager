package com.taskmanagerbackend.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Long columnId;
    private String columnName;
    private Long assigneeId;
    private String assigneeName;
    private String priority;
    private LocalDate dueDate;
    private Integer position;
    private LocalDateTime createdAt;
}