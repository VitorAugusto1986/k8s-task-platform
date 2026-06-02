package com.vitor.cloudtask.cloudnativetaskplatform.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    @Test
    @DisplayName("No-args constructor creates a Task with null/default fields")
    void noArgsConstructor_defaultValues() {
        Task task = new Task();

        assertThat(task.getId()).isNull();
        assertThat(task.getTitle()).isNull();
        assertThat(task.isDone()).isFalse();
    }

    @Test
    @DisplayName("All-args constructor sets all fields correctly")
    void allArgsConstructor_setsAllFields() {
        Task task = new Task(1L, "Write tests", true);

        assertThat(task.getId()).isEqualTo(1L);
        assertThat(task.getTitle()).isEqualTo("Write tests");
        assertThat(task.isDone()).isTrue();
    }

    @Test
    @DisplayName("Setters update fields correctly")
    void setters_updateFields() {
        Task task = new Task();
        task.setId(5L);
        task.setTitle("Deploy");
        task.setDone(true);

        assertThat(task.getId()).isEqualTo(5L);
        assertThat(task.getTitle()).isEqualTo("Deploy");
        assertThat(task.isDone()).isTrue();
    }

    @Test
    @DisplayName("equals() returns true for two tasks with identical fields")
    void equals_identicalTasks() {
        Task t1 = new Task(1L, "Deploy", false);
        Task t2 = new Task(1L, "Deploy", false);

        assertThat(t1).isEqualTo(t2);
    }

    @Test
    @DisplayName("equals() returns false when fields differ")
    void equals_differentTasks() {
        Task t1 = new Task(1L, "Deploy", false);
        Task t2 = new Task(2L, "Deploy", false);

        assertThat(t1).isNotEqualTo(t2);
    }

    @Test
    @DisplayName("hashCode() is equal for two tasks with identical fields")
    void hashCode_identicalTasks() {
        Task t1 = new Task(1L, "Deploy", false);
        Task t2 = new Task(1L, "Deploy", false);

        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
    }

    @Test
    @DisplayName("toString() contains all field values")
    void toString_containsAllFields() {
        Task task = new Task(3L, "Review PR", true);
        String str = task.toString();

        assertThat(str).contains("3", "Review PR", "true");
    }
}
