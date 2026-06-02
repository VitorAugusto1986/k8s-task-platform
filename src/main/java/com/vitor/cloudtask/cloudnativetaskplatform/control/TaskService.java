package com.vitor.cloudtask.cloudnativetaskplatform.control;

import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final List<Task> tasks = new ArrayList<>();
    private long counter = 1;

    public List<Task> getAll() {
        return tasks;
    }

    public Task create(String title) {
        Task task = new Task(counter++, title, false);
        tasks.add(task);
        return task;
    }

    public Task toggle(Long id) {
        return tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .map(t -> {
                    t.setDone(!t.isDone());
                    return t;
                })
                .orElse(null);
    }
}