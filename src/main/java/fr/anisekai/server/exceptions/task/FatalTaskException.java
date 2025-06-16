package fr.anisekai.server.exceptions.task;

public class FatalTaskException extends RuntimeException {

    public FatalTaskException(String message) {

        super(message);
    }

}
