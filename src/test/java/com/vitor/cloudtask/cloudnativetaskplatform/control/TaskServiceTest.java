package com.vitor.cloudtask.cloudnativetaskplatform.control;

import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;
import com.vitor.cloudtask.cloudnativetaskplatform.exception.TaskNotFoundException;
import com.vitor.cloudtask.cloudnativetaskplatform.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    // ── getAll ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAll() returns empty list when no tasks exist")
    void getAll_emptyList() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.getAll()).isEmpty();
    }

    @Test
    @DisplayName("getAll() returns all tasks from repository")
    void getAll_returnsAllTasks() {
        when(repository.findAll()).thenReturn(List.of(
                new Task(1L, "Task A", false),
                new Task(2L, "Task B", true)
        ));

        assertThat(service.getAll()).hasSize(2);
    }

    // ── getById ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getById() returns the task when ID exists")
    void getById_returnsTask() {
        Task task = new Task(1L, "Find me", false);
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        Task result = service.getById(1L);

        assertThat(result.getTitle()).isEqualTo("Find me");
    }

    @Test
    @DisplayName("getById() throws TaskNotFoundException when ID does not exist")
    void getById_throwsWhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create() saves a task with the given title and done=false")
    void create_savesTask() {
        Task saved = new Task(1L, "Buy milk", false);
        when(repository.save(any(Task.class))).thenReturn(saved);

        Task result = service.create("Buy milk");

        assertThat(result.getTitle()).isEqualTo("Buy milk");
        assertThat(result.isDone()).isFalse();
        assertThat(result.getId()).isEqualTo(1L);
        verify(repository).save(any(Task.class));
    }

    // ── toggle ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("toggle() marks an undone task as done")
    void toggle_marksDoneWhenUndone() {
        Task task = new Task(1L, "Deploy app", false);
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(task)).thenReturn(task);

        Task result = service.toggle(1L);

        assertThat(result.isDone()).isTrue();
        verify(repository).save(task);
    }

    @Test
    @DisplayName("toggle() marks a done task as undone")
    void toggle_marksUndoneWhenDone() {
        Task task = new Task(1L, "Deploy app", true);
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(task)).thenReturn(task);

        Task result = service.toggle(1L);

        assertThat(result.isDone()).isFalse();
    }

    @Test
    @DisplayName("toggle() throws TaskNotFoundException when ID does not exist")
    void toggle_throwsWhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.toggle(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("toggle() does not affect other tasks")
    void toggle_doesNotAffectOtherTasks() {
        Task taskA = new Task(1L, "Task A", false);
        Task taskB = new Task(2L, "Task B", false);
        when(repository.findById(1L)).thenReturn(Optional.of(taskA));
        when(repository.save(taskA)).thenReturn(taskA);

        service.toggle(1L);

        assertThat(taskA.isDone()).isTrue();
        assertThat(taskB.isDone()).isFalse();
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete() removes the task by ID")
    void delete_removesTask() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete() throws TaskNotFoundException when ID does not exist")
    void delete_throwsWhenNotFound() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }
}
