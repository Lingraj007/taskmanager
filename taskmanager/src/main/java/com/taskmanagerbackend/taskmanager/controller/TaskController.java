package com.taskmanagerbackend.taskmanager.controller;

import com.taskmanagerbackend.taskmanager.dto.MoveTaskRequest;
import com.taskmanagerbackend.taskmanager.dto.TaskRequest;
import com.taskmanagerbackend.taskmanager.dto.TaskResponse;
import com.taskmanagerbackend.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest request, Authentication authentication) {
        try {
            String email = authentication.getName();
            TaskResponse response = taskService.createTask(request, email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/column/{columnId}")
    public ResponseEntity<?> getTasksByColumn(@PathVariable Long columnId) {
        List<TaskResponse> tasks = taskService.getTasksByColumn(columnId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        try {
            TaskResponse response = taskService.updateTask(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/move")
    public ResponseEntity<?> moveTask(@PathVariable Long id, @Valid @RequestBody MoveTaskRequest request) {
        try {
            TaskResponse response = taskService.moveTask(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}