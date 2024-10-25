package me.anisekai.api.plannifier.exceptions;

import me.anisekai.globals.exceptions.SilentDiscordException;
import org.jetbrains.annotations.NotNull;

public class NotSchedulableException extends SilentDiscordException {

    public NotSchedulableException() {

        super("Not free spot found or the spot indicated is not available for scheduling.");
    }

    @Override
    public @NotNull String getFriendlyMessage() {

        return "Impossible de plannifier cet évènement: La date indiquée est indisponible ou aucun créneau n'a été trouvé dans une durée raisonable.";
    }

}
