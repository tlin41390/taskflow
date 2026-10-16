package com.taskflow.repository;

import com.taskflow.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TaskRepositoryTest {
    
    @Autowired 
    private TestEntityManager entityManager;

    @Autowired 
    private TaskRepository taskRepository;

    @Test 
    void savingATaskAssignsAnId() {
        Task saved = taskRepository.save(new Task("Write lesson notes"));

        assertThat(saved.getId()).isNotNull();
    }

    @Test 
    void findByDoneReturnsOnlyMatchingTasks() {
        Task doneTask = new Task("Already finished");
        doneTask.setDone(true);
        entityManager.persistAndFlush(doneTask);
        entityManager.persistAndFlush(new Task("Still open"));
        entityManager.persistAndFlush(new Task("Also open"));

        List<Task> done = taskRepository.findByDone(true);
        List<Task> open = taskRepository.findByDone(false);

        assertThat(done).extracting(Task::getTitle).containsExactly("Already finished");
        assertThat(open).hasSize(2);
    }
}
