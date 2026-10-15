package com.taskflow.controller;

import com.taskflow.dto.UpdateTaskRequest;
import com.taskflow.dto.CreateTaskRequest;
import com.taskflow.model.Task;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.taskflow.exception.TaskNotFoundException;
import java.util.List;

@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public List<Task> tasks() {
        return taskService.listTasks();
    }

    @PostMapping("/tasks")
    public Task addTask(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request.title());
    }

    @GetMapping("/tasks/{id}")
    public Task getTask(@PathVariable Long id) {
        return taskService.getTask(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @PutMapping("/tasks/{id}")
    public Task updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request) {
        return taskService.updateTask(id, request.title(), request.done())
            .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskService.deleteTask(id)) {
            throw new TaskNotFoundException(id);
        }
        return ResponseEntity.noContent().build();
    }
}
