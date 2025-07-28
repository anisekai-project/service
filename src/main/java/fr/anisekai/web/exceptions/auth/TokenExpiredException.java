package fr.anisekai.web.exceptions.auth;

import fr.anisekai.web.exceptions.WebException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class TokenExpiredException extends WebException {

    public TokenExpiredException(UUID uuid) {

        super(
                HttpStatus.UNAUTHORIZED,
                "The token %s is expired.".formatted(uuid),
                "Invalid JWT token."
        );
    }

}
