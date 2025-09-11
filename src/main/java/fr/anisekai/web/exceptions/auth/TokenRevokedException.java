package fr.anisekai.web.exceptions.auth;

import fr.anisekai.web.exceptions.WebException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class TokenRevokedException extends WebException {

    public TokenRevokedException(UUID uuid) {

        super(
                HttpStatus.UNAUTHORIZED,
                "The token %s is revoked.".formatted(uuid),
                "Invalid JWT token."
        );
    }

}
