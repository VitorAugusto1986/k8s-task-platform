package com.vitor.cloudtask.cloudnativetaskplatform.boundary;

import com.vitor.cloudtask.cloudnativetaskplatform.control.TaskService;
import com.vitor.cloudtask.cloudnativetaskplatform.dto.CreateTaskRequest;
import com.vitor.cloudtask.cloudnativetaskplatform.dto.TaskMapper;
import com.vitor.cloudtask.cloudnativetaskplatform.dto.TaskResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<TaskResponse> getAll() {
        return TaskMapper.toResponseList(service.getAll());
    }

    @PostMapping
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        return TaskMapper.toResponse(service.create(request.getTitle()));
    }

    @PutMapping("/{id}")
    public TaskResponse toggle(@PathVariable Long id) {
        return TaskMapper.toResponse(service.toggle(id));
    }
}