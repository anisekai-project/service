package me.anisekai.server.repositories;

import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.Broadcast;
import me.anisekai.server.enums.BroadcastStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BroadcastRepository extends JpaRepository<Broadcast, Long> {

    Optional<Broadcast> findFirstByWatchTargetOrderByStartingAtAsc(Anime watchTarget);

    List<Broadcast> findAllByStatus(BroadcastStatus status);

    long countBroadcastByStartingAtAfter(ZonedDateTime startingAtAfter);

    Optional<Broadcast> findByEventId(Long eventId);

}
