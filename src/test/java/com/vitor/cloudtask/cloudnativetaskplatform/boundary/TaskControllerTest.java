package com.vitor.cloudtask.cloudnativetaskplatform.boundary;

import com.vitor.cloudtask.cloudnativetaskplatform.control.TaskService;
import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;
import com.vitor.cloudtask.cloudnativetaskplatform.exception.TaskNotFoundException;
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
        when(taskService.getAll()).thenReturn(List.of(
                new Task(1L, "Task A", false),
                new Task(2L, "Task B", true)
        ));

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
    @DisplayName("GET /tasks/{id} returns 200 and the task when found")
    void getById_returnsTask() throws Exception {
        when(taskService.getById(1L)).thenReturn(new Task(1L, "Buy milk", false));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Buy milk"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    @DisplayName("GET /tasks/{id} returns 404 when task does not exist")
    void getById_notFound_returns404() throws Exception {
        when(taskService.getById(999L)).thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Task not found: 999"));
    }

    @Test
    @DisplayName("POST /tasks returns 201 and the created task")
    void create_returnsCreatedTask() throws Exception {
        when(taskService.create("Buy milk")).thenReturn(new Task(1L, "Buy milk", false));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Buy milk\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Buy milk"))
                .andExpect(jsonPath("$.done").value(false));

        verify(taskService, times(1)).create("Buy milk");
    }

    @Test
    @DisplayName("POST /tasks returns 400 when title is blank")
    void create_blankTitle_returns400() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /tasks returns 400 when title exceeds 100 characters")
    void create_titleTooLong_returns400() throws Exception {
        String longTitle = "a".repeat(101);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + longTitle + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("PUT /tasks/{id} returns 200 with the toggled task")
    void toggle_returnsUpdatedTask() throws Exception {
        when(taskService.toggle(1L)).thenReturn(new Task(1L, "Buy milk", true));

        mockMvc.perform(put("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.done").value(true));

        verify(taskService, times(1)).toggle(1L);
    }

    @Test
    @DisplayName("PUT /tasks/{id} returns 404 when task does not exist")
    void toggle_notFound_returns404() throws Exception {
        when(taskService.toggle(999L)).thenThrow(new TaskNotFoundException(999L));

        mockMvc.perform(put("/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(taskService, times(1)).toggle(999L);
    }

    @Test
    @DisplayName("DELETE /tasks/{id} returns 204 when task is deleted")
    void delete_returns204() throws Exception {
        doNothing().when(taskService).delete(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("DELETE /tasks/{id} returns 404 when task does not exist")
    void delete_notFound_returns404() throws Exception {
        doThrow(new TaskNotFoundException(999L)).when(taskService).delete(999L);

        mockMvc.perform(delete("/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(taskService, times(1)).delete(999L);
    }
}
