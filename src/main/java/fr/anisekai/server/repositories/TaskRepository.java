package fr.anisekai.server.repositories;

import fr.anisekai.wireless.remote.enums.TaskStatus;
import fr.anisekai.server.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.status = :status ORDER BY t.priority DESC, t.createdAt ASC LIMIT 1")
    Optional<Task> getFirst(TaskStatus status);

    List<Task> findAllByNameAndStatus(String name, TaskStatus status);

    long countTaskByStatus(TaskStatus status);

    Optional<Task> findByNameAndStatusIn(String name, List<TaskStatus> scheduled);

    List<Task> findAllByStatus(TaskStatus status);

}
