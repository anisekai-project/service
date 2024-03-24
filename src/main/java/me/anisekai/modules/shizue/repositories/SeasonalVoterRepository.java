package me.anisekai.modules.shizue.repositories;

import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.entities.SeasonalVoter;
import me.anisekai.modules.shizue.entities.keys.SeasonalVoterKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonalVoterRepository extends JpaRepository<SeasonalVoter, SeasonalVoterKey> {

    List<SeasonalVoter> findAllBySeasonalSelection(SeasonalSelection seasonalSelection);

}
