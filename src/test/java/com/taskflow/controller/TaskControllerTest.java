package com.taskflow.controller;

import com.taskflow.model.Task;
import com.taskflow.service.TaskService;
import com.taskflow.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void listTasksReturnsWhatServiceProvides() throws Exception {
        when(taskService.listTasks(null)).thenReturn(List.of(new Task("Write lesson notes")));

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Write lesson notes"));
    }

    @Test
    @WithMockUser
    void blankTitleIsRejectedBeforeReachingService() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\"}"))
            .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any());
    }

    @Test 
    void unknownIdReturnsStructuredErrorBody() throws Exception {
        when(taskService.getTask(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Task 99 not found"));
    }

    @Test 
    void unauthenticatedPostIsRejected() throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Write lesson notes\"}"))
            .andExpect(status().isUnauthorized());
        
        verify(taskService, never()).createTask(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteRequiresAdminRole() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
            .andExpect(status().isForbidden());
        
        verify(taskService, never()).deleteTask(any());
    }
}
