package com.vitor.cloudtask.cloudnativetaskplatform.repository;

import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository repository;

    @Test
    @DisplayName("save() persists a task and assigns a generated ID")
    void save_persistsTaskWithId() {
        Task task = new Task(null, "Buy milk", false);

        Task saved = repository.save(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Buy milk");
        assertThat(saved.isDone()).isFalse();
    }

    @Test
    @DisplayName("findAll() returns all persisted tasks")
    void findAll_returnsAllTasks() {
        repository.save(new Task(null, "Task A", false));
        repository.save(new Task(null, "Task B", true));

        List<Task> tasks = repository.findAll();

        assertThat(tasks).hasSize(2);
    }

    @Test
    @DisplayName("findById() returns the task when it exists")
    void findById_returnsTask() {
        Task saved = repository.save(new Task(null, "Find me", false));

        Optional<Task> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Find me");
    }

    @Test
    @DisplayName("findById() returns empty when task does not exist")
    void findById_returnsEmpty() {
        Optional<Task> found = repository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("save() updates an existing task")
    void save_updatesTask() {
        Task saved = repository.save(new Task(null, "Original", false));
        saved.setDone(true);

        Task updated = repository.save(saved);

        assertThat(updated.isDone()).isTrue();
    }

    @Test
    @DisplayName("deleteById() removes the task")
    void deleteById_removesTask() {
        Task saved = repository.save(new Task(null, "Delete me", false));

        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("existsById() returns true when task exists")
    void existsById_returnsTrueWhenExists() {
        Task saved = repository.save(new Task(null, "Exists", false));

        assertThat(repository.existsById(saved.getId())).isTrue();
    }

    @Test
    @DisplayName("existsById() returns false when task does not exist")
    void existsById_returnsFalseWhenMissing() {
        assertThat(repository.existsById(999L)).isFalse();
    }
}

