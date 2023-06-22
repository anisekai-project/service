package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.Torrent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TorrentRepository extends JpaRepository<Torrent, Integer> {

    boolean existsByInfoHash(String infoHash);

}
