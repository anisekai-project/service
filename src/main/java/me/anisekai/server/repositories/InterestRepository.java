package me.anisekai.server.repositories;

import fr.anisekai.wireless.remote.keys.InterestKey;
import me.anisekai.server.entities.Anime;
import me.anisekai.server.entities.DiscordUser;
import me.anisekai.server.entities.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<Interest, InterestKey> {

    List<Interest> findByAnime(Anime anime);

    List<Interest> findByUser(DiscordUser user);

    List<Interest> findByAnimeIn(Collection<Anime> anime);

}
