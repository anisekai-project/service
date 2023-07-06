package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.entities.SeasonalVoter;
import me.anisekai.toshiko.entities.keys.SeasonalVoterKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonalVoterRepository extends JpaRepository<SeasonalVoter, SeasonalVoterKey> {

    List<SeasonalVoter> findAllBySeasonalSelection(SeasonalSelection seasonalSelection);

}
