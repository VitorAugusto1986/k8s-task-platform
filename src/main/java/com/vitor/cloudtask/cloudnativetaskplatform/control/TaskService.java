package com.vitor.cloudtask.cloudnativetaskplatform.control;

import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;
import com.vitor.cloudtask.cloudnativetaskplatform.exception.TaskNotFoundException;
import com.vitor.cloudtask.cloudnativetaskplatform.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;

    public List<Task> getAll() {
        log.info("Fetching all tasks");
        return repository.findAll();
    }

    public Task getById(Long id) {
        log.info("Fetching task id={}", id);
        return repository.findById(id).orElseThrow(() -> {
            log.warn("Task not found id={}", id);
            return new TaskNotFoundException(id);
        });
    }

    public Task create(String title) {
        Task saved = repository.save(new Task(null, title, false));
        log.info("Created task id={} title='{}'", saved.getId(), saved.getTitle());
        return saved;
    }

    public Task toggle(Long id) {
        Task task = getById(id);
        task.setDone(!task.isDone());
        Task saved = repository.save(task);
        log.info("Toggled task id={} done={}", saved.getId(), saved.isDone());
        return saved;
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Delete failed, task not found id={}", id);
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
        log.info("Deleted task id={}", id);
    }
}

