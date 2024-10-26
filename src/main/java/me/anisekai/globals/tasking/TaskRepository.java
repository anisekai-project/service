package me.anisekai.globals.tasking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.state = :state ORDER BY t.createdAt ASC")
    Optional<Task> getFirst(TaskState state);

    boolean existsByNameAndStateIn(String name, Collection<TaskState> state);

}
