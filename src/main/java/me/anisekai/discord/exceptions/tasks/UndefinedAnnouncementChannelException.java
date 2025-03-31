package me.anisekai.discord.exceptions.tasks;

public class UndefinedAnnouncementChannelException extends IllegalStateException {

    public UndefinedAnnouncementChannelException() {

        super("The announcement channel has not been setup correctly.");
    }

}
