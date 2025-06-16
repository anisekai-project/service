package fr.anisekai.server.exceptions.task;

import org.json.JSONException;

public class TaskArgumentException extends Exception {

    public TaskArgumentException(String message, JSONException ex) {

        super(message, ex);
    }

}
