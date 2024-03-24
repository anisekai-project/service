package me.anisekai.toshiko.utils;

import me.anisekai.modules.linn.entities.Anime;

import java.util.function.Supplier;

public final class FakeService {

    private FakeService() {}

    public static Anime fakeAnime(long id, String name, long watched, long total, long duration) {

        Anime anime = new Anime();
        anime.setId(id);
        anime.setName(name);
        anime.setWatched(watched);
        anime.setTotal(total);
        anime.setEpisodeDuration(duration);

        return anime;
    }

    public static final Supplier<Anime> FAKE_ANIME_ONE = () -> fakeAnime(1, "AnimeOne", 0, 12, 24);
    public static final Supplier<Anime> FAKE_ANIME_TWO = () -> fakeAnime(2, "AnimeOne", 0, 12, 24);


}
