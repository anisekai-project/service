package me.anisekai.modules.shizue.repositories;

import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.entities.Interest;
import me.anisekai.modules.shizue.entities.keys.UserAnimeAssocKey;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface InterestRepository extends JpaRepository<Interest, UserAnimeAssocKey> {

    Optional<Interest> findByAnimeAndUser(Anime anime, DiscordUser user);

    Set<Interest> findAllByAnime(Anime anime);

    @Query("SELECT i FROM Interest i WHERE i.user.active = true")
    List<Interest> findAllActive();

    @Deprecated
    @NotNull List<Interest> findAll();

}
