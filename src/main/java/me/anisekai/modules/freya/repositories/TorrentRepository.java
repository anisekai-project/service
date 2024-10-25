package me.anisekai.modules.freya.repositories;

import me.anisekai.modules.freya.entities.Torrent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TorrentRepository extends JpaRepository<Torrent, Integer> {

}
