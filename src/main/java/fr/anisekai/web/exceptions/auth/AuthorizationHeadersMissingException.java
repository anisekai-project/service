package fr.anisekai.web.exceptions.auth;

import fr.anisekai.web.exceptions.WebException;
import org.springframework.http.HttpStatus;

public class AuthorizationHeadersMissingException extends WebException {

    public AuthorizationHeadersMissingException() {

        super(
                HttpStatus.UNAUTHORIZED,
                "Authorization header is missing or not a bearer token",
                "Authorization header is missing or not a bearer token"
        );
    }

}
