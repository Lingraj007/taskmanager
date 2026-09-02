package com.taskmanagerbackend.taskmanager.service;

import com.taskmanagerbackend.taskmanager.dto.ProjectRequest;
import com.taskmanagerbackend.taskmanager.dto.ProjectResponse;
import com.taskmanagerbackend.taskmanager.entity.Board;
import com.taskmanagerbackend.taskmanager.entity.BoardColumn;
import com.taskmanagerbackend.taskmanager.entity.Project;
import com.taskmanagerbackend.taskmanager.entity.ProjectMember;
import com.taskmanagerbackend.taskmanager.entity.User;
import com.taskmanagerbackend.taskmanager.repository.BoardColumnRepository;
import com.taskmanagerbackend.taskmanager.repository.BoardRepository;
import com.taskmanagerbackend.taskmanager.repository.ProjectMemberRepository;
import com.taskmanagerbackend.taskmanager.repository.ProjectRepository;
import com.taskmanagerbackend.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardColumnRepository boardColumnRepository;

    public ProjectResponse createProject(ProjectRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setCreatedBy(creator);

        Project saved = projectRepository.save(project);

        // Automatically add the creator as a project member with MANAGER role
        ProjectMember member = new ProjectMember();
        member.setProject(saved);
        member.setUser(creator);
        member.setRoleInProject(User.Role.MANAGER);
        projectMemberRepository.save(member);

        // Automatically create a default board with 3 columns
        Board board = new Board();
        board.setProject(saved);
        board.setName("Main Board");
        Board savedBoard = boardRepository.save(board);

        createDefaultColumn(savedBoard, "To Do", 0);
        createDefaultColumn(savedBoard, "In Progress", 1);
        createDefaultColumn(savedBoard, "Done", 2);

        return toResponse(saved);
    }

    private void createDefaultColumn(Board board, String name, int position) {
        BoardColumn column = new BoardColumn();
        column.setBoard(board);
        column.setName(name);
        column.setPosition(position);
        boardColumnRepository.save(column);
    }

    public List<ProjectResponse> getMyProjects(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ProjectMember> memberships = projectMemberRepository.findByUserId(user.getId());

        return memberships.stream()
                .map(pm -> toResponse(pm.getProject()))
                .collect(Collectors.toList());
    }

    public ProjectResponse getProjectById(Long projectId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new RuntimeException("You are not a member of this project");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return toResponse(project);
    }

    public ProjectResponse updateProject(Long projectId, ProjectRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Only the project creator can update this project");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        return toResponse(projectRepository.save(project));
    }

    public void deleteProject(Long projectId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Only the project creator can delete this project");
        }

        projectRepository.delete(project);
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedBy().getName(),
                project.getCreatedBy().getId(),
                project.getCreatedAt()
        );
    }
}