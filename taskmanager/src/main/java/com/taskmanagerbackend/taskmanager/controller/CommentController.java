package com.taskmanagerbackend.taskmanager.controller;

import com.taskmanagerbackend.taskmanager.dto.CommentRequest;
import com.taskmanagerbackend.taskmanager.dto.CommentResponse;
import com.taskmanagerbackend.taskmanager.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> addComment(@Valid @RequestBody CommentRequest request, Authentication authentication) {
        try {
            String email = authentication.getName();
            CommentResponse response = commentService.addComment(request, email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<?> getCommentsByTask(@PathVariable Long taskId) {
        List<CommentResponse> comments = commentService.getCommentsByTask(taskId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, Authentication authentication) {
        try {
            String email = authentication.getName();
            commentService.deleteComment(id, email);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}