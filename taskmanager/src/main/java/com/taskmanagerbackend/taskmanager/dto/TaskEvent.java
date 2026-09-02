package com.taskmanagerbackend.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskEvent {
    private String eventType; // "TASK_CREATED", "TASK_UPDATED", "TASK_MOVED", "TASK_DELETED"
    private TaskResponse task;
}