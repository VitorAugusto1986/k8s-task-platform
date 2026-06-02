package com.vitor.cloudtask.cloudnativetaskplatform.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateTaskRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("No-args constructor creates request with null title")
    void noArgsConstructor_defaultValues() {
        UpdateTaskRequest request = new UpdateTaskRequest();

        assertThat(request.getTitle()).isNull();
    }

    @Test
    @DisplayName("All-args constructor sets title")
    void allArgsConstructor_setsTitle() {
        UpdateTaskRequest request = new UpdateTaskRequest("Rename task");

        assertThat(request.getTitle()).isEqualTo("Rename task");
    }

    @Test
    @DisplayName("Setter updates title")
    void setter_updatesTitle() {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Refactor module");

        assertThat(request.getTitle()).isEqualTo("Refactor module");
    }

    @Test
    @DisplayName("Validation fails when title is blank")
    void validation_failsWhenTitleBlank() {
        UpdateTaskRequest request = new UpdateTaskRequest("   ");

        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Title is required");
    }

    @Test
    @DisplayName("Validation fails when title exceeds 100 characters")
    void validation_failsWhenTitleTooLong() {
        UpdateTaskRequest request = new UpdateTaskRequest("a".repeat(101));

        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Title must have at most 100 characters");
    }

    @Test
    @DisplayName("Validation passes when title is valid")
    void validation_passesWhenTitleValid() {
        UpdateTaskRequest request = new UpdateTaskRequest("Updated title");

        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}

