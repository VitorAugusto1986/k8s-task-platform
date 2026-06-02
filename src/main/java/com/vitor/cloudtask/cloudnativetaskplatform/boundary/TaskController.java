package com.vitor.cloudtask.cloudnativetaskplatform.boundary;

import com.vitor.cloudtask.cloudnativetaskplatform.control.TaskService;
import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;
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
    public List<Task> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Task create(@RequestParam String title) {
        return service.create(title);
    }

    @PutMapping("/{id}")
    public Task toggle(@PathVariable Long id) {
        return service.toggle(id);
    }
}