package me.anisekai.api.plannifier.exceptions;

import me.anisekai.discord.exceptions.SilentDiscordException;
import org.jetbrains.annotations.NotNull;

public class GroupedScheduleException extends SilentDiscordException {

    public GroupedScheduleException(String message) {

        super(message);
    }

    @Override
    public @NotNull String getFriendlyMessage() {

        return "Impossible de programmer tous les évènements: Au moins un évènement entre en conflit dans le programme.";
    }

}
