package me.anisekai.discord.exceptions.tasks;

public class UndefinedAnnouncementRoleException extends IllegalStateException {

    public UndefinedAnnouncementRoleException() {

        super("The announcement role has not been setup correctly.");
    }

}
