package com.vitor.cloudtask.cloudnativetaskplatform.control;

import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;
import com.vitor.cloudtask.cloudnativetaskplatform.exception.TaskNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TaskService {

    private final List<Task> tasks = new ArrayList<>();
    private long counter = 1;

    public List<Task> getAll() {
        log.info("Fetching all tasks, total={}", tasks.size());
        return tasks;
    }

    public Task getById(Long id) {
        log.info("Fetching task id={}", id);
        return tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Task not found id={}", id);
                    return new TaskNotFoundException(id);
                });
    }

    public Task create(String title) {
        Task task = new Task(counter++, title, false);
        tasks.add(task);
        log.info("Created task id={} title='{}'", task.getId(), task.getTitle());
        return task;
    }

    public Task toggle(Long id) {
        Task task = tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Toggle failed, task not found id={}", id);
                    return new TaskNotFoundException(id);
                });
        task.setDone(!task.isDone());
        log.info("Toggled task id={} done={}", task.getId(), task.isDone());
        return task;
    }

    public void delete(Long id) {
        Task task = tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Delete failed, task not found id={}", id);
                    return new TaskNotFoundException(id);
                });
        tasks.remove(task);
        log.info("Deleted task id={}", id);
    }
}