package com.taskmanagerbackend.taskmanager.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class MoveTaskRequest {

    @NotNull(message = "Target column id is required")
    private Long targetColumnId;

    @NotNull(message = "New position is required")
    private Integer newPosition;
}