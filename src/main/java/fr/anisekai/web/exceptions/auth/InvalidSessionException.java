package fr.anisekai.web.exceptions.auth;

import fr.anisekai.web.exceptions.WebException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class InvalidSessionException extends WebException {

    public InvalidSessionException(UUID uuid) {

        super(
                HttpStatus.UNAUTHORIZED,
                "No session is associated to %s".formatted(uuid),
                "Invalid JWT token."
        );
    }

}
