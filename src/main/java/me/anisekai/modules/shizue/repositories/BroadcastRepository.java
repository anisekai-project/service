package me.anisekai.modules.shizue.repositories;

import me.anisekai.modules.shizue.entities.Broadcast;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BroadcastRepository extends JpaRepository<Broadcast, Long> {

    List<Broadcast> findAllByStatusInAndScheduledIsTrue(Collection<ScheduledEvent.@NotNull Status> status);

    List<Broadcast> findAllByStatus(ScheduledEvent.Status status);

    Optional<Broadcast> findByEventId(Long eventId);

}
