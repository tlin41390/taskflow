package com.taskflow.service;

import com.taskflow.model.Task;
import com.taskflow.repository.TaskRepository;
import com.taskflow.client.NotificationClient;
import org.springframework.stereotype.Service;
import java.util.List; 
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final NotificationClient notificationClient;

    public TaskService(TaskRepository taskRepository, NotificationClient notificationClient) {
        this.taskRepository = taskRepository;
        this.notificationClient = notificationClient;
    }

    public List<Task> listTasks(Boolean done) {
        return done == null ? taskRepository.findAll() : taskRepository.findByDone(done);
    }

    public Task createTask(String title) {
        Task task =  taskRepository.save(new Task(title));
        notificationClient.notifyTaskCreated(task.getId(), task.getTitle());
        return task;
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
