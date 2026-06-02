package com.vitor.cloudtask.cloudnativetaskplatform.dto;

import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;

import java.util.List;
import java.util.stream.Collectors;

public final class TaskMapper {

    private TaskMapper() {
    }

    public static TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }
        return new TaskResponse(task.getId(), task.getTitle(), task.isDone());
    }

    public static List<TaskResponse> toResponseList(List<Task> tasks) {
        return tasks.stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }
}

