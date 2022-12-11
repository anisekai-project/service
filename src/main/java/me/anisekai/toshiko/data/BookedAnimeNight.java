package me.anisekai.toshiko.data;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.exceptions.animes.InvalidAnimeProgressException;
import me.anisekai.toshiko.interfaces.AnimeNightMeta;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;

public class BookedAnimeNight implements AnimeNightMeta {

    public static BookedAnimeNight with(Anime anime, OffsetTime time, long amount) {

        OffsetDateTime startDateTime = ZonedDateTime.now()
                                                    .withHour(time.getHour())
                                                    .withMinute(time.getMinute())
                                                    .withSecond(0)
                                                    .withNano(0)
                                                    .toOffsetDateTime();

        return with(anime, startDateTime, amount);
    }



    public static BookedAnimeNight with(Anime anime, DayOfWeek day, OffsetTime time, long amount) {

        OffsetDateTime startDateTime = ZonedDateTime.now()
                                                    .withHour(time.getHour())
                                                    .withMinute(time.getMinute())
                                                    .withSecond(0)
                                                    .withNano(0)
                                                    .toOffsetDateTime();

        while (startDateTime.getDayOfWeek() != day) {
            startDateTime = startDateTime.plusDays(1);
        }

        return with(anime, startDateTime, amount);
    }

    public static BookedAnimeNight with(Anime anime, OffsetDateTime startTime, long amount) {

        return new BookedAnimeNight(anime, startTime, amount);
    }

    public static BookedAnimeNight after(AnimeNightMeta meta, OffsetDateTime startTime, long amount) {

        return new BookedAnimeNight(meta.getAnime(), startTime, amount, meta.getLastEpisode() + 1);
    }

    private Anime          anime;
    private long           firstEpisode;
    private long           lastEpisode;
    private long           amount;
    private OffsetDateTime startDateTime;
    private OffsetDateTime endDateTime;

    public BookedAnimeNight(Anime anime, OffsetDateTime startTime, long amount) {

        this(anime, startTime, amount, anime.getWatched() + 1);
    }

    public BookedAnimeNight(Anime anime, OffsetDateTime startTime, long amount, long firstEpisode) {

        this.anime        = anime;
        this.amount       = amount;
        this.setFirstEpisode(firstEpisode);
        this.setStartDateTime(startTime);

        if (this.anime.getTotal() > 0 && this.lastEpisode > this.anime.getTotal()) {
            throw new InvalidAnimeProgressException();
        }
    }

    @Override
    public Anime getAnime() {

        return this.anime;
    }

    @Override
    public void setAnime(Anime anime) {

        this.anime = anime;
    }

    @Override
    public long getFirstEpisode() {

        return this.firstEpisode;
    }

    @Override
    public void setFirstEpisode(long firstEpisode) {

        this.firstEpisode = firstEpisode;

        if (this.getAnime().getTotal() > 0) {
            this.lastEpisode = Math.min(this.firstEpisode + this.amount - 1, this.anime.getTotal());
            this.amount      = this.lastEpisode - this.firstEpisode + 1;
        } else {
            this.lastEpisode = this.firstEpisode + (this.amount - 1);
        }
    }

    @Override
    public long getLastEpisode() {

        return this.lastEpisode;
    }

    @Override
    public void setLastEpisode(long lastEpisode) {

        this.lastEpisode = lastEpisode;
        this.amount      = this.getLastEpisode() - this.getFirstEpisode();
    }

    @Override
    public long getAmount() {

        return this.amount;
    }

    @Override
    public void setAmount(long amount) {

        this.amount      = amount;
        this.lastEpisode = this.getFirstEpisode() + this.getAmount();
    }

    public OffsetDateTime getStartDateTime() {

        return this.startDateTime;
    }

    public void setStartDateTime(OffsetDateTime startDateTime) {

        this.startDateTime = startDateTime;

        long openingEndingDuration = (this.getAmount() - 1) * 3; // OP/ED usually 1m30 each
        long totalWatchTime        = this.getAmount() * this.getAnime().getEpisodeDuration();

        this.endDateTime = this.startDateTime.plusMinutes(totalWatchTime - openingEndingDuration);
    }

    public OffsetDateTime getEndDateTime() {

        return this.endDateTime;
    }

    public void setEndDateTime(OffsetDateTime endDateTime) {

        // Cannot be defined manually.
        throw new IllegalStateException("Can't modify endTime manually. This is a computed property.");
    }

}
