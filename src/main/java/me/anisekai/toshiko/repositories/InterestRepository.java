package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.entities.keys.InterestKey;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<Interest, InterestKey> {

    @Query("SELECT i FROM Interest i WHERE i.user.banned = false AND i.anime = :anime")
    List<Interest> findAllActiveByAnime(Anime anime);

    @Query("SELECT i FROM Interest i WHERE i.user.banned = false")
    List<Interest> findAllActive();

    @Deprecated
    List<Interest> findAllByAnime(Anime anime);

    @Deprecated
    @NotNull List<Interest> findAll();
}
