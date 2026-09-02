package com.taskmanagerbackend.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class BoardResponse {
    private Long id;
    private String name;
    private List<BoardColumnResponse> columns;
}