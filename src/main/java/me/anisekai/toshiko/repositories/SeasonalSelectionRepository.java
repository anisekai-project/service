package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.SeasonalSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonalSelectionRepository extends JpaRepository<SeasonalSelection, Long> {
}
