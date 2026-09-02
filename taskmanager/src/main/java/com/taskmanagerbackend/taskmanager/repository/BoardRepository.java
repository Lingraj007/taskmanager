package com.taskmanagerbackend.taskmanager.repository;

import com.taskmanagerbackend.taskmanager.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByProjectId(Long projectId);
}