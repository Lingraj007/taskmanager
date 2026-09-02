package com.taskmanagerbackend.taskmanager.service;

import com.taskmanagerbackend.taskmanager.dto.MoveTaskRequest;
import com.taskmanagerbackend.taskmanager.dto.TaskRequest;
import com.taskmanagerbackend.taskmanager.dto.TaskResponse;
import com.taskmanagerbackend.taskmanager.entity.BoardColumn;
import com.taskmanagerbackend.taskmanager.entity.Task;
import com.taskmanagerbackend.taskmanager.entity.User;
import com.taskmanagerbackend.taskmanager.repository.BoardColumnRepository;
import com.taskmanagerbackend.taskmanager.repository.TaskRepository;
import com.taskmanagerbackend.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BoardColumnRepository boardColumnRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public TaskResponse createTask(TaskRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BoardColumn column = boardColumnRepository.findById(request.getColumnId())
                .orElseThrow(() -> new RuntimeException("Column not found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setColumn(column);
        task.setCreatedBy(creator);

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
        }

        if (request.getPriority() != null) {
            task.setPriority(Task.Priority.valueOf(request.getPriority().toUpperCase()));
        } else {
            task.setPriority(Task.Priority.MEDIUM);
        }

        task.setDueDate(request.getDueDate());

        List<Task> existingTasks = taskRepository.findByColumnIdOrderByPosition(column.getId());
        int nextPosition = existingTasks.isEmpty() ? 0 : existingTasks.size();
        task.setPosition(nextPosition);

        Task saved = taskRepository.save(task);
        TaskResponse response = toResponse(saved);

        broadcastTaskEvent(column.getBoard().getProject().getId(), "TASK_CREATED", response);

        return response;
    }

    public List<TaskResponse> getTasksByColumn(Long columnId) {
        return taskRepository.findByColumnIdOrderByPosition(columnId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse updateTask(Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);
        }

        if (request.getPriority() != null) {
            task.setPriority(Task.Priority.valueOf(request.getPriority().toUpperCase()));
        }

        task.setDueDate(request.getDueDate());

        Task saved = taskRepository.save(task);
        TaskResponse response = toResponse(saved);

        broadcastTaskEvent(task.getColumn().getBoard().getProject().getId(), "TASK_UPDATED", response);

        return response;
    }

    public TaskResponse moveTask(Long taskId, MoveTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        BoardColumn targetColumn = boardColumnRepository.findById(request.getTargetColumnId())
                .orElseThrow(() -> new RuntimeException("Target column not found"));

        Long oldColumnId = task.getColumn().getId();
        int oldPosition = task.getPosition();

        List<Task> oldColumnTasks = taskRepository.findByColumnIdOrderByPosition(oldColumnId);
        for (Task t : oldColumnTasks) {
            if (!t.getId().equals(taskId) && t.getPosition() > oldPosition) {
                t.setPosition(t.getPosition() - 1);
                taskRepository.save(t);
            }
        }

        List<Task> newColumnTasks = taskRepository.findByColumnIdOrderByPosition(request.getTargetColumnId());
        for (Task t : newColumnTasks) {
            if (t.getPosition() >= request.getNewPosition()) {
                t.setPosition(t.getPosition() + 1);
                taskRepository.save(t);
            }
        }

        task.setColumn(targetColumn);
        task.setPosition(request.getNewPosition());

        Task saved = taskRepository.save(task);
        TaskResponse response = toResponse(saved);

        broadcastTaskEvent(targetColumn.getBoard().getProject().getId(), "TASK_MOVED", response);

        return response;
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Long projectId = task.getColumn().getBoard().getProject().getId();
        Long deletedTaskId = task.getId();

        List<Task> columnTasks = taskRepository.findByColumnIdOrderByPosition(task.getColumn().getId());
        for (Task t : columnTasks) {
            if (t.getPosition() > task.getPosition()) {
                t.setPosition(t.getPosition() - 1);
                taskRepository.save(t);
            }
        }

        taskRepository.delete(task);

        messagingTemplate.convertAndSend(
                "/topic/project/" + projectId,
                new TaskEventMessage("TASK_DELETED", deletedTaskId, null)
        );
    }

    private void broadcastTaskEvent(Long projectId, String eventType, TaskResponse task) {
        messagingTemplate.convertAndSend(
                "/topic/project/" + projectId,
                new TaskEventMessage(eventType, task.getId(), task)
        );
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getColumn().getId(),
                task.getColumn().getName(),
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getAssignee() != null ? task.getAssignee().getName() : null,
                task.getPriority() != null ? task.getPriority().name() : null,
                task.getDueDate(),
                task.getPosition(),
                task.getCreatedAt()
        );
    }

    // Simple inner class representing the WebSocket message payload
    public static class TaskEventMessage {
        public String eventType;
        public Long taskId;
        public TaskResponse task;

        public TaskEventMessage(String eventType, Long taskId, TaskResponse task) {
            this.eventType = eventType;
            this.taskId = taskId;
            this.task = task;
        }
    }
}