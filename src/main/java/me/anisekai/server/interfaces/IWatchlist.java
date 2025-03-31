package me.anisekai.server.interfaces;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.server.enums.AnimeStatus;

/**
 * Interface representing an object holding data about a watchlist.
 */
public interface IWatchlist extends IEntity<AnimeStatus> {

    /**
     * Retrieve the discord message id to which this {@link IWatchlist} is associated.
     *
     * @return A discord message id.
     */
    Long getMessageId();

    /**
     * Define the discord message id to which this {@link IWatchlist} is associated.
     *
     * @param messageId
     *         A discord message id.
     */
    void setMessageId(Long messageId);

    /**
     * Retrieve this {@link IWatchlist}'s order, defining the priority when sending the message on discord.
     *
     * @return A priority
     */
    int getOrder();

    /**
     * Define this {@link IWatchlist}'s order, defining the priority when sending the message on discord.
     *
     * @param order
     *         A priority
     */
    void setOrder(int order);

}

