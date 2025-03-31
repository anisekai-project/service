package me.anisekai.server.repositories;

import me.anisekai.server.entities.Selection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectionRepository extends JpaRepository<Selection, Long> {

}
