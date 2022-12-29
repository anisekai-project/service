package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.AnimeNight;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimeNightRepository extends JpaRepository<AnimeNight, Long> {

    List<AnimeNight> findAllByAnimeAndStatusIn(Anime anime, Collection<ScheduledEvent.@NotNull Status> status);

    List<AnimeNight> findAllByStatusIn(Collection<ScheduledEvent.@NotNull Status> status);

    List<AnimeNight> findAllByEventIdIsNullAndStatusIsNull();

    Optional<AnimeNight> findByEventId(Long eventId);

    void deleteByEventId(Long eventId);

}
