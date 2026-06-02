package com.vitor.cloudtask.cloudnativetaskplatform.dto;

import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMapperTest {

    @Test
    @DisplayName("toResponse maps entity fields to response DTO")
    void toResponse_mapsFields() {
        Task task = new Task(1L, "Buy milk", false);

        TaskResponse response = TaskMapper.toResponse(task);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Buy milk");
        assertThat(response.isDone()).isFalse();
    }

    @Test
    @DisplayName("toResponse returns null when input task is null")
    void toResponse_returnsNullWhenTaskNull() {
        TaskResponse response = TaskMapper.toResponse(null);

        assertThat(response).isNull();
    }

    @Test
    @DisplayName("toResponseList maps all task entities")
    void toResponseList_mapsAllTasks() {
        List<Task> tasks = List.of(
                new Task(1L, "Task A", false),
                new Task(2L, "Task B", true)
        );

        List<TaskResponse> responses = TaskMapper.toResponseList(tasks);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(0).getTitle()).isEqualTo("Task A");
        assertThat(responses.get(0).isDone()).isFalse();
        assertThat(responses.get(1).getId()).isEqualTo(2L);
        assertThat(responses.get(1).getTitle()).isEqualTo("Task B");
        assertThat(responses.get(1).isDone()).isTrue();
    }
}

