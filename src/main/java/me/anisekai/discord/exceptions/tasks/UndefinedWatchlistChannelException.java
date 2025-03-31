package me.anisekai.discord.exceptions.tasks;

public class UndefinedWatchlistChannelException extends IllegalStateException {

    public UndefinedWatchlistChannelException() {

        super("The watchlist channel has not been setup correctly.");
    }

}
