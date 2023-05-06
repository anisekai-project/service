package me.anisekai.toshiko.repositories;

import me.anisekai.toshiko.entities.DiscordUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<DiscordUser, Long> {

    List<DiscordUser> findAllByActiveIsTrue();

}
