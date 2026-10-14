package com.taskflow.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity 
public class Task {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private boolean done;

    protected Task() {
        // JPA does not require a no-argument constructor to rebuild rows into objects
    }

    public Task(String title) {
        this.title = title;
        this.done = false;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
