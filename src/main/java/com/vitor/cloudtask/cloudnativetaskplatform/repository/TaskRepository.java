package com.vitor.cloudtask.cloudnativetaskplatform.repository;

import com.vitor.cloudtask.cloudnativetaskplatform.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}

