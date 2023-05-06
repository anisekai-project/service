package me.anisekai.toshiko.data.anime;

import me.anisekai.toshiko.entities.Anime;

/**
 * Create a new {@link AnimeImportResult} instance.
 *
 * @param anime
 *         The {@link Anime} that was imported or updated.
 * @param state
 *         The {@link State} indicating if the {@link Anime} was created or updated.
 */
public record AnimeImportResult(Anime anime, State state) {

    public enum State {
        /**
         * The {@link Anime} was created.
         */
        CREATED,
        /**
         * The {@link Anime} was updated.
         */
        UPDATED
    }

}
