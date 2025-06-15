package fr.anisekai.library.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.File;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StoreBreakOutException extends RuntimeException {

    private final File file;

    public StoreBreakOutException(String message, File file) {

        super(message);
        this.file = file;
    }

    public File getFile() {

        return this.file;
    }

}
