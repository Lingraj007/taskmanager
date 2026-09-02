package com.taskmanagerbackend.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String createdByName;
    private Long createdById;
    private LocalDateTime createdAt;
}