package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.ScheduledEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledEventRepository extends JpaRepository<ScheduledEvent, Integer> {}
