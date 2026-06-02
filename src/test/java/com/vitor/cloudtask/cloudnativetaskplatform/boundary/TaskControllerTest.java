package com.vitor.cloudtask.cloudnativetaskplatform.boundary;

import com.vitor.cloudtask.cloudnativetaskplatform.control.TaskService;
import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    @DisplayName("GET /tasks returns 200 and an empty JSON array when no tasks exist")
    void getAll_emptyList() throws Exception {
        when(taskService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /tasks returns 200 and all tasks as JSON")
    void getAll_withTasks() throws Exception {
        List<Task> tasks = List.of(
                new Task(1L, "Task A", false),
                new Task(2L, "Task B", true)
        );
        when(taskService.getAll()).thenReturn(tasks);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Task A"))
                .andExpect(jsonPath("$[0].done").value(false))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Task B"))
                .andExpect(jsonPath("$[1].done").value(true));
    }

    @Test
    @DisplayName("POST /tasks creates a new task from JSON body and returns 200")
    void create_returnsCreatedTask() throws Exception {
        Task created = new Task(1L, "Buy milk", false);
        when(taskService.create("Buy milk")).thenReturn(created);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Buy milk\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Buy milk"))
                .andExpect(jsonPath("$.done").value(false));

        verify(taskService, times(1)).create("Buy milk");
    }

    @Test
    @DisplayName("PUT /tasks/{id} toggles the task and returns 200 with the updated task")
    void toggle_returnsUpdatedTask() throws Exception {
        Task toggled = new Task(1L, "Buy milk", true);
        when(taskService.toggle(1L)).thenReturn(toggled);

        mockMvc.perform(put("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.done").value(true));

        verify(taskService, times(1)).toggle(1L);
    }

    @Test
    @DisplayName("PUT /tasks/{id} returns 200 with null body when task is not found")
    void toggle_taskNotFound_returnsEmpty() throws Exception {
        when(taskService.toggle(999L)).thenReturn(null);

        mockMvc.perform(put("/tasks/999"))
                .andExpect(status().isOk());

        verify(taskService, times(1)).toggle(999L);
    }
}
