package me.anisekai.api.plannifier.exceptions;

import me.anisekai.globals.exceptions.SilentDiscordException;
import org.jetbrains.annotations.NotNull;

public class InvalidSchedulingDurationException extends SilentDiscordException {

    public InvalidSchedulingDurationException() {
        super("Unable to schedule an event with an invalid duration.");
    }

    @Override
    public @NotNull String getFriendlyMessage() {

        return "Impossible de plannifier un évènement avec une durée invalide.";
    }

}
