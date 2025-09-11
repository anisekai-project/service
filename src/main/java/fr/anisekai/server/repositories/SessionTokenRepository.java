package fr.anisekai.server.repositories;

import fr.anisekai.server.entities.SessionToken;
import fr.anisekai.web.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionTokenRepository extends JpaRepository<SessionToken, UUID> {

    Optional<SessionToken> findByIdAndTypeIn(UUID id, Collection<TokenType> type);

}
