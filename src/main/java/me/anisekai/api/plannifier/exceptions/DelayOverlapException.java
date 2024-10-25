package me.anisekai.api.plannifier.exceptions;

import me.anisekai.globals.exceptions.SilentDiscordException;
import org.jetbrains.annotations.NotNull;

public class DelayOverlapException extends SilentDiscordException {

    public DelayOverlapException(String message, Throwable cause) {

        super(message, cause);
    }

    public DelayOverlapException(String message) {

        super(message);
    }

    @Override
    public @NotNull String getFriendlyMessage() {

        return "Impossible de décaler les évènements: Cela entrerait en conflit avec des séances déjà programmée.";
    }

}
