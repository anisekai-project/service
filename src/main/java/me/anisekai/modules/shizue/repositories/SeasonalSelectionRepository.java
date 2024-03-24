package me.anisekai.modules.shizue.repositories;

import me.anisekai.modules.shizue.entities.SeasonalSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonalSelectionRepository extends JpaRepository<SeasonalSelection, Long> {

}
