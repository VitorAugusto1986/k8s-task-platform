package com.vitor.cloudtask.cloudnativetaskplatform.boundary;

import com.vitor.cloudtask.cloudnativetaskplatform.control.TaskService;
import com.vitor.cloudtask.cloudnativetaskplatform.dto.CreateTaskRequest;
import com.vitor.cloudtask.cloudnativetaskplatform.dto.TaskMapper;
import com.vitor.cloudtask.cloudnativetaskplatform.dto.TaskResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<TaskResponse> getAll() {
        log.info("GET /tasks");
        return TaskMapper.toResponseList(service.getAll());
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Long id) {
        log.info("GET /tasks/{}", id);
        return TaskMapper.toResponse(service.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        log.info("POST /tasks title='{}'", request.getTitle());
        return TaskMapper.toResponse(service.create(request.getTitle()));
    }

    @PutMapping("/{id}")
    public TaskResponse toggle(@PathVariable Long id) {
        log.info("PUT /tasks/{}", id);
        return TaskMapper.toResponse(service.toggle(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /tasks/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}