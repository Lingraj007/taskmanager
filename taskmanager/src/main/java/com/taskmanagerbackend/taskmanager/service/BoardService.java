package com.taskmanagerbackend.taskmanager.service;

import com.taskmanagerbackend.taskmanager.dto.BoardColumnResponse;
import com.taskmanagerbackend.taskmanager.dto.BoardResponse;
import com.taskmanagerbackend.taskmanager.entity.Board;
import com.taskmanagerbackend.taskmanager.entity.BoardColumn;
import com.taskmanagerbackend.taskmanager.repository.BoardColumnRepository;
import com.taskmanagerbackend.taskmanager.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardColumnRepository boardColumnRepository;

    public BoardResponse getBoardByProjectId(Long projectId) {
        Board board = boardRepository.findByProjectId(projectId)
                .orElseThrow(() -> new RuntimeException("Board not found for this project"));

        List<BoardColumn> columns = boardColumnRepository.findByBoardIdOrderByPosition(board.getId());

        List<BoardColumnResponse> columnResponses = columns.stream()
                .map(c -> new BoardColumnResponse(c.getId(), c.getName(), c.getPosition()))
                .collect(Collectors.toList());

        return new BoardResponse(board.getId(), board.getName(), columnResponses);
    }
}