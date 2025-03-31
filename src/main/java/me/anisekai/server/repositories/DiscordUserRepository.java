package me.anisekai.server.repositories;

import me.anisekai.server.entities.DiscordUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscordUserRepository extends JpaRepository<DiscordUser, Long> {

    List<DiscordUser> findAllByActiveIsTrue();

}
