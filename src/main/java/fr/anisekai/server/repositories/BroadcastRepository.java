package fr.anisekai.server.repositories;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Broadcast;
import fr.anisekai.wireless.remote.enums.BroadcastStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BroadcastRepository extends JpaRepository<Broadcast, Long> {

    List<Broadcast> findAllByStatusIn(Collection<BroadcastStatus> statuses);

    List<Broadcast> findAllByStatus(BroadcastStatus status);

    long countBroadcastByStartingAtAfter(ZonedDateTime startingAtAfter);

    Optional<Broadcast> findByEventId(Long eventId);

    @Query("select count(b) from Broadcast b where b.watchTarget.id = :id and b.startingAt < :startingAt and b.status IN :statuses")
    long countPreviousOf(Long id, ZonedDateTime startingAt, Collection<BroadcastStatus> statuses);

    List<Broadcast> findByWatchTargetAndStartingAtAfterOrderByStartingAtAsc(Anime watchTarget, ZonedDateTime startingAt);


}
