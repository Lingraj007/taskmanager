package com.taskmanagerbackend.taskmanager.controller;

import com.taskmanagerbackend.taskmanager.dto.BoardResponse;
import com.taskmanagerbackend.taskmanager.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getBoardByProject(@PathVariable Long projectId) {
        try {
            BoardResponse response = boardService.getBoardByProjectId(projectId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}