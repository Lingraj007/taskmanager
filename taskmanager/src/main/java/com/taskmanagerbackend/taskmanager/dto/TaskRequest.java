package com.taskmanagerbackend.taskmanager.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Column id is required")
    private Long columnId;

    private Long assigneeId;

    private String priority; // LOW, MEDIUM, HIGH

    private LocalDate dueDate;
}