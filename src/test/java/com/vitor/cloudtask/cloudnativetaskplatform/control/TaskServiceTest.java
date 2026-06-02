package com.vitor.cloudtask.cloudnativetaskplatform.control;

import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;
import com.vitor.cloudtask.cloudnativetaskplatform.exception.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskServiceTest {

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService();
    }

    @Test
    @DisplayName("getAll() returns empty list when no tasks have been created")
    void getAll_emptyInitially() {
        List<Task> result = service.getAll();

        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("getAll() returns all created tasks")
    void getAll_returnsAllTasks() {
        service.create("Task A");
        service.create("Task B");

        List<Task> result = service.getAll();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("create() returns a task with the given title")
    void create_returnsTaskWithTitle() {
        Task task = service.create("Buy milk");

        assertThat(task.getTitle()).isEqualTo("Buy milk");
    }

    @Test
    @DisplayName("create() returns a task with done=false by default")
    void create_taskIsNotDoneByDefault() {
        Task task = service.create("Buy milk");

        assertThat(task.isDone()).isFalse();
    }

    @Test
    @DisplayName("create() assigns auto-incremented IDs starting from 1")
    void create_assignsIncrementingIds() {
        Task first  = service.create("First");
        Task second = service.create("Second");

        assertThat(first.getId()).isEqualTo(1L);
        assertThat(second.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("create() adds the task to the internal list")
    void create_addsTaskToList() {
        service.create("Write tests");

        assertThat(service.getAll()).hasSize(1);
        assertThat(service.getAll().get(0).getTitle()).isEqualTo("Write tests");
    }

    @Test
    @DisplayName("toggle() marks an undone task as done")
    void toggle_marksDoneWhenUndone() {
        Task task = service.create("Deploy app");

        Task toggled = service.toggle(task.getId());

        assertThat(toggled).isNotNull();
        assertThat(toggled.isDone()).isTrue();
    }

    @Test
    @DisplayName("toggle() marks a done task as undone")
    void toggle_marksUndoneWhenDone() {
        Task task = service.create("Deploy app");
        service.toggle(task.getId()); // now done = true

        Task toggled = service.toggle(task.getId()); // now done = false

        assertThat(toggled).isNotNull();
        assertThat(toggled.isDone()).isFalse();
    }

    @Test
    @DisplayName("toggle() throws TaskNotFoundException when the task ID does not exist")
    void toggle_throwsWhenNotFound() {
        assertThatThrownBy(() -> service.toggle(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("toggle() only modifies the targeted task")
    void toggle_doesNotAffectOtherTasks() {
        Task taskA = service.create("Task A");
        Task taskB = service.create("Task B");

        service.toggle(taskA.getId());

        assertThat(taskA.isDone()).isTrue();
        assertThat(taskB.isDone()).isFalse();
    }

    // ── getById ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getById() returns the task when ID exists")
    void getById_returnsTask() {
        Task created = service.create("Find me");

        Task found = service.getById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Find me");
    }

    @Test
    @DisplayName("getById() throws TaskNotFoundException when ID does not exist")
    void getById_throwsWhenNotFound() {
        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete() removes the task from the list")
    void delete_removesTask() {
        Task created = service.create("To be deleted");

        service.delete(created.getId());

        assertThat(service.getAll()).isEmpty();
    }

    @Test
    @DisplayName("delete() throws TaskNotFoundException when ID does not exist")
    void delete_throwsWhenNotFound() {
        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("999");
    }
}
