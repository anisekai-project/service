package me.anisekai.toshiko.utils;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.exceptions.animes.InvalidAnimeProgressException;

public final class Animes {

    private Animes() {}


    public static void requireValidProgression(Anime anime, long amount) {

        requireValidProgression(anime, amount, false);
    }

    public static void requireValidProgression(Anime anime, long amount, boolean strict) {

        boolean validTotal   = anime.getTotal() > 0 || !strict;
        boolean boundedTotal = anime.getWatched() + amount <= anime.getTotal() || validTotal;
        boolean validAmount  = amount > 0;

        if (!boundedTotal || !validAmount) {
            throw new InvalidAnimeProgressException();
        }
    }


}
