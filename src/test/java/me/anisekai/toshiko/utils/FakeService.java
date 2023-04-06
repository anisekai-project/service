package me.anisekai.toshiko.utils;

import me.anisekai.toshiko.entities.Anime;

import java.util.function.Supplier;

public class FakeService {

    public static Anime fakeAnime(String name, long watched, long total, long duration) {

        Anime anime = new Anime();
        anime.setName(name);
        anime.setWatched(watched);
        anime.setTotal(total);
        anime.setEpisodeDuration(duration);

        return anime;
    }

    public static final Supplier<Anime> FAKE_ANIME_ONE = () -> fakeAnime("AnimeOne", 0, 12, 24);
    public static final Supplier<Anime> FAKE_ANIME_TWO = () -> fakeAnime("AnimeOne", 0, 12, 24);


}
