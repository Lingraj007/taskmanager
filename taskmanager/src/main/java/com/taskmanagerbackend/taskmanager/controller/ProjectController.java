package com.taskmanagerbackend.taskmanager.controller;

import com.taskmanagerbackend.taskmanager.dto.ProjectRequest;
import com.taskmanagerbackend.taskmanager.dto.ProjectResponse;
import com.taskmanagerbackend.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectRequest request, Authentication authentication) {
        try {
            String email = authentication.getName();
            ProjectResponse response = projectService.createProject(request, email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyProjects(Authentication authentication) {
        String email = authentication.getName();
        List<ProjectResponse> projects = projectService.getMyProjects(email);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id, Authentication authentication) {
        try {
            String email = authentication.getName();
            ProjectResponse response = projectService.getProjectById(id, email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication
    ) {
        try {
            String email = authentication.getName();
            ProjectResponse response = projectService.updateProject(id, request, email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id, Authentication authentication) {
        try {
            String email = authentication.getName();
            projectService.deleteProject(id, email);
            return ResponseEntity.ok("Project deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}