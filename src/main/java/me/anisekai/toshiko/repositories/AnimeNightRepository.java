package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.AnimeNight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimeNightRepository extends JpaRepository<AnimeNight, Long> {}
