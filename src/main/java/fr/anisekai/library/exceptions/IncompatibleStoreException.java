package fr.anisekai.library.exceptions;

import fr.anisekai.annotations.FatalTask;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) @FatalTask
public class IncompatibleStoreException extends RuntimeException {

    public IncompatibleStoreException(String message) {

        super(message);
    }

}
