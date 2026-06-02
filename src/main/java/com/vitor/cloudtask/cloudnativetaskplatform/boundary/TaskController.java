package com.vitor.cloudtask.cloudnativetaskplatform.boundary;

import com.vitor.cloudtask.cloudnativetaskplatform.control.TaskService;
import com.vitor.cloudtask.cloudnativetaskplatform.dto.CreateTaskRequest;
import com.vitor.cloudtask.cloudnativetaskplatform.dto.TaskResponse;
import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<TaskResponse> getAll() {
        return service.getAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        return toResponse(service.create(request.getTitle()));
    }

    @PutMapping("/{id}")
    public TaskResponse toggle(@PathVariable Long id) {
        Task task = service.toggle(id);
        return task == null ? null : toResponse(task);
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.isDone());
    }
}