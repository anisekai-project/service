package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.entities.SeasonalVote;
import me.anisekai.toshiko.entities.keys.UserAnimeVoteAssocKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonalVoteRepository extends JpaRepository<SeasonalVote, UserAnimeVoteAssocKey> {

    List<SeasonalVote> findAllBySeasonalSelection(SeasonalSelection seasonalSelection);

}
