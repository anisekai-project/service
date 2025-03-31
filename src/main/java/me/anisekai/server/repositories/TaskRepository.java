package me.anisekai.server.repositories;

import me.anisekai.server.entities.Task;
import me.anisekai.server.enums.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.state = :state ORDER BY t.priority DESC, t.createdAt ASC LIMIT 1")
    Optional<Task> getFirst(TaskState state);

    List<Task> findAllByNameAndState(String name, TaskState state);

    long countTaskByState(TaskState state);

    Optional<Task> findByNameAndStateIn(String name, List<TaskState> scheduled);

}
