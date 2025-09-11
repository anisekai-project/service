package fr.anisekai.web.exceptions.auth;

import fr.anisekai.web.exceptions.WebException;
import org.springframework.http.HttpStatus;

public class BearerParsingException extends WebException {

    public BearerParsingException(Throwable exception) {

        super(
                HttpStatus.UNAUTHORIZED,
                "Unable to parse JWT token.",
                "Invalid JWT token.",
                exception
        );
    }

    public BearerParsingException(String message) {

        super(
                HttpStatus.UNAUTHORIZED,
                message,
                "Invalid JWT token."
        );
    }

}
