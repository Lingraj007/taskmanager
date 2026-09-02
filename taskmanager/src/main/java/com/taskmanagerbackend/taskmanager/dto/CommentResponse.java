package com.taskmanagerbackend.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long taskId;
    private String content;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
}