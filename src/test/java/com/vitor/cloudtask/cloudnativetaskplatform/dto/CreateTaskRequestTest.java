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

class CreateTaskRequestTest {

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
        CreateTaskRequest request = new CreateTaskRequest();

        assertThat(request.getTitle()).isNull();
    }

    @Test
    @DisplayName("All-args constructor sets title")
    void allArgsConstructor_setsTitle() {
        CreateTaskRequest request = new CreateTaskRequest("Buy milk");

        assertThat(request.getTitle()).isEqualTo("Buy milk");
    }

    @Test
    @DisplayName("Setter updates title")
    void setter_updatesTitle() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Write tests");

        assertThat(request.getTitle()).isEqualTo("Write tests");
    }

    @Test
    @DisplayName("Validation fails when title is null")
    void validation_failsWhenTitleIsNull() {
        CreateTaskRequest request = new CreateTaskRequest(null);

        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Title is required");
    }

    @Test
    @DisplayName("Validation fails when title is blank")
    void validation_failsWhenTitleIsBlank() {
        CreateTaskRequest request = new CreateTaskRequest("   ");

        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Title is required");
    }

    @Test
    @DisplayName("Validation fails when title exceeds 100 characters")
    void validation_failsWhenTitleTooLong() {
        CreateTaskRequest request = new CreateTaskRequest("a".repeat(101));

        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Title must have at most 100 characters");
    }

    @Test
    @DisplayName("Validation passes when title is valid")
    void validation_passesWhenTitleValid() {
        CreateTaskRequest request = new CreateTaskRequest("Valid title");

        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}

