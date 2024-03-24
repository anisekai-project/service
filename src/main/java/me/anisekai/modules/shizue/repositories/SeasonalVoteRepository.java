package me.anisekai.modules.shizue.repositories;

import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.entities.SeasonalVote;
import me.anisekai.modules.shizue.entities.keys.UserAnimeVoteAssocKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonalVoteRepository extends JpaRepository<SeasonalVote, UserAnimeVoteAssocKey> {

    List<SeasonalVote> findAllBySeasonalSelection(SeasonalSelection seasonalSelection);

    List<SeasonalVote> findAllBySeasonalSelectionAndUser(SeasonalSelection seasonalSelection, DiscordUser user);

}
