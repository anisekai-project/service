package fr.anisekai.web.exceptions;

public class MalformedAuthenticationTokenException extends RuntimeException {

    public MalformedAuthenticationTokenException(String message) {

        super(message);
    }

    public MalformedAuthenticationTokenException(String message, Throwable cause) {

        super(message, cause);
    }

}
