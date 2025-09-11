package fr.anisekai.server.repositories;

import fr.anisekai.server.entities.Task;
import fr.anisekai.wireless.remote.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.factoryName IN :factoryNames ORDER BY t.priority DESC, t.id LIMIT 1")
    Optional<Task> findNextOf(TaskStatus status, Collection<String> factoryNames);

    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.factoryName NOT IN :factoryNames ORDER BY t.priority DESC, t.id LIMIT 1")
    Optional<Task> findNextNotOf(TaskStatus status, Collection<String> factoryNames);

    List<Task> findAllByNameAndStatus(String name, TaskStatus status);

    Optional<Task> findByNameAndStatusIn(String name, List<TaskStatus> scheduled);

    List<Task> findAllByStatus(TaskStatus status);

}
