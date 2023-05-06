package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.SeasonalVote;
import me.anisekai.toshiko.entities.keys.UserAnimeAssocKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonalVoteRepository extends JpaRepository<SeasonalVote, UserAnimeAssocKey> {
}
