package me.anisekai.api.plannifier.exceptions;

import me.anisekai.discord.exceptions.SilentDiscordException;
import org.jetbrains.annotations.NotNull;

public class SchedulingUpdatingException extends SilentDiscordException {

    public SchedulingUpdatingException(Throwable e) {

        super("The parent service managing scheduled was unable to save the event.", e);
    }

    @Override
    public @NotNull String getFriendlyMessage() {

        return "La sauvegarde d'un évènement a échouée.";
    }

}
