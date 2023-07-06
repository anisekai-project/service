package me.anisekai.toshiko.data;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.exceptions.animes.InvalidAnimeProgressException;
import me.anisekai.toshiko.interfaces.AnimeNightMeta;

import java.time.OffsetDateTime;

public class BookedAnimeNight implements AnimeNightMeta {

    private final Anime          anime;
    private       long           firstEpisode;
    private       long           lastEpisode;
    private final long           amount;
    private       OffsetDateTime startDateTime;
    private       OffsetDateTime endDateTime;

    public BookedAnimeNight(AnimeNightMeta meta) {

        this.anime         = meta.getAnime();
        this.firstEpisode  = meta.getFirstEpisode();
        this.lastEpisode   = meta.getLastEpisode();
        this.amount        = meta.getAmount();
        this.startDateTime = meta.getStartDateTime();
        this.endDateTime   = meta.getEndDateTime();
    }

    public BookedAnimeNight(Anime anime, long firstEpisode, long lastEpisode, long amount, OffsetDateTime startDateTime, OffsetDateTime endDateTime) {

        this.anime         = anime;
        this.firstEpisode  = firstEpisode;
        this.lastEpisode   = lastEpisode;
        this.amount        = amount;
        this.startDateTime = startDateTime;
        this.endDateTime   = endDateTime;
    }

    public BookedAnimeNight(Anime anime, OffsetDateTime startTime, long amount) {

        this(anime, startTime, amount, anime.getWatched() + 1);
    }

    public BookedAnimeNight(Anime anime, OffsetDateTime startTime, long amount, long firstEpisode) {

        this.anime        = anime;
        this.amount       = amount;
        this.firstEpisode = firstEpisode;
        this.lastEpisode  = firstEpisode + (amount - 1);

        if (this.anime.getTotal() > 0 && this.lastEpisode > this.anime.getTotal()) {
            throw new InvalidAnimeProgressException();
        }

        this.setStartDateTime(startTime);
    }

    @Override
    public Anime getAnime() {

        return this.anime;
    }

    @Override
    public long getFirstEpisode() {

        return this.firstEpisode;
    }

    @Override
    public void setFirstEpisode(long firstEpisode) {

        this.firstEpisode = firstEpisode;
        this.lastEpisode  = firstEpisode + (this.getAmount() - 1);
    }

    @Override
    public long getLastEpisode() {

        return this.lastEpisode;
    }

    @Override
    public void setLastEpisode(long lastEpisode) {

        this.lastEpisode  = lastEpisode;
        this.firstEpisode = lastEpisode - this.getAmount();
    }

    @Override
    public long getAmount() {

        return this.amount;
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

        // Ignore this, it doesn't matter much in this class
    }

}
