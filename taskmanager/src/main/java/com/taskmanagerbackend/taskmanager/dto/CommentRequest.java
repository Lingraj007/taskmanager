package com.taskmanagerbackend.taskmanager.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CommentRequest {

    @NotNull(message = "Task id is required")
    private Long taskId;

    @NotBlank(message = "Content is required")
    private String content;
}