package me.anisekai.server.interfaces;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;
import me.anisekai.server.events.interest.InterestLevelUpdatedEvent;
import me.anisekai.server.keys.UserAnimeKey;

/**
 * Interface representing an object holding data about the interest level of a {@link IDiscordUser} for an
 * {@link IAnime}.
 */
public interface IInterest<O extends IDiscordUser, T extends IAnime<O>> extends IEntity<UserAnimeKey> {

    /**
     * Retrieve the {@link IDiscordUser} to which this {@link IInterest} belongs.
     *
     * @return A {@link IDiscordUser}.
     */
    O getUser();

    /**
     * Define the {@link IDiscordUser} to which this {@link IInterest} belongs.
     *
     * @param user
     *         A {@link IDiscordUser}.
     */
    void setUser(O user);

    /**
     * Retrieve the {@link IAnime} referenced by this {@link IInterest}.
     *
     * @return An {@link IAnime}
     */
    T getAnime();

    /**
     * Retrieve the {@link IAnime} referenced by this {@link IInterest}.
     *
     * @param anime
     *         An {@link IAnime}
     */
    void setAnime(T anime);

    /**
     * Retrieve this {@link IInterest} weight in the leaderboard calculations. Values are ranging from -2 to 2.
     *
     * @return The level power.
     */
    long getLevel();

    /**
     * Define this {@link IInterest} weight in the leaderboard calculations. Values are ranging from -2 to 2.
     *
     * @param level
     *         The level power.
     */
    @TriggerEvent(InterestLevelUpdatedEvent.class)
    void setLevel(long level);

    @Override
    default UserAnimeKey getId() {

        return UserAnimeKey.of(this.getAnime(), this.getUser());
    }

}
