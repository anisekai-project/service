package fr.anisekai.server.repositories;

import fr.anisekai.wireless.remote.keys.VoterKey;
import fr.anisekai.server.entities.DiscordUser;
import fr.anisekai.server.entities.Selection;
import fr.anisekai.server.entities.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoterRepository extends JpaRepository<Voter, VoterKey> {

    Optional<Voter> findBySelectionAndUser(Selection selection, DiscordUser user);

    List<Voter> findBySelection(Selection selection);

}
