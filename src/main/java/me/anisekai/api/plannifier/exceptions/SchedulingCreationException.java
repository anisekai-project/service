package me.anisekai.api.plannifier.exceptions;

import me.anisekai.discord.exceptions.SilentDiscordException;
import org.jetbrains.annotations.NotNull;

public class SchedulingCreationException extends SilentDiscordException {

    public SchedulingCreationException(Throwable e) {

        super("The parent service managing scheduled event denied or was unable to save the new event.", e);
    }

    @Override
    public @NotNull String getFriendlyMessage() {

        return "L'évènement a été créé mais sa sauvegarde a échouée.";
    }

}
