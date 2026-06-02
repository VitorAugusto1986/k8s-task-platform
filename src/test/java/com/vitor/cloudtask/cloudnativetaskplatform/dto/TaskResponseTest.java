package com.vitor.cloudtask.cloudnativetaskplatform.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskResponseTest {

    @Test
    @DisplayName("No-args constructor creates response with null/default fields")
    void noArgsConstructor_defaultValues() {
        TaskResponse response = new TaskResponse();

        assertThat(response.getId()).isNull();
        assertThat(response.getTitle()).isNull();
        assertThat(response.isDone()).isFalse();
    }

    @Test
    @DisplayName("All-args constructor sets all fields")
    void allArgsConstructor_setsAllFields() {
        TaskResponse response = new TaskResponse(10L, "Deploy", true);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("Deploy");
        assertThat(response.isDone()).isTrue();
    }

    @Test
    @DisplayName("Setters update fields")
    void setters_updateFields() {
        TaskResponse response = new TaskResponse();
        response.setId(20L);
        response.setTitle("Review PR");
        response.setDone(true);

        assertThat(response.getId()).isEqualTo(20L);
        assertThat(response.getTitle()).isEqualTo("Review PR");
        assertThat(response.isDone()).isTrue();
    }

    @Test
    @DisplayName("equals and hashCode match for identical values")
    void equalsAndHashCode_identicalValues() {
        TaskResponse first = new TaskResponse(1L, "Task", false);
        TaskResponse second = new TaskResponse(1L, "Task", false);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    @DisplayName("toString contains field values")
    void toString_containsFieldValues() {
        TaskResponse response = new TaskResponse(5L, "Observe", true);

        assertThat(response.toString()).contains("5", "Observe", "true");
    }
}

