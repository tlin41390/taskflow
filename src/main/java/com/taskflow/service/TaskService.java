package com.taskflow.service;

import com.taskflow.model.Task;
import com.taskflow.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.util.List; 
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    public Task createTask(String title) {
        return taskRepository.save(new Task(title));
    }

    public Optional<Task> getTask(Long id) {
        return taskRepository.findById(id);
    }

    public Optional<Task> updateTask(Long id, String title, boolean done) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(title);
            task.setDone(done);
            return taskRepository.save(task);
        });
    }

    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        taskRepository.deleteById(id);
        return true;
    }
}
