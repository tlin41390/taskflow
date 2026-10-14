package com.taskflow.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTaskRequest(
    @NotBlank(message = "title must not be blank")
    String title,
    boolean done
) { 
}
