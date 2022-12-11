package me.anisekai.toshiko.interfaces;

import me.anisekai.toshiko.entities.Anime;

import java.time.OffsetDateTime;

public interface AnimeNightMeta {

    Anime getAnime();

    void setAnime(Anime anime);

    long getFirstEpisode();

    void setFirstEpisode(long firstEpisode);

    long getLastEpisode();

    void setLastEpisode(long lastEpisode);

    long getAmount();

    void setAmount(long amount);

    OffsetDateTime getStartDateTime();

    void setStartDateTime(OffsetDateTime startDateTime);

    OffsetDateTime getEndDateTime();

    void setEndDateTime(OffsetDateTime endDateTime);

    default String asEventDescription() {

        long amount = this.getAmount();

        if (amount == 1) {
            return String.format("**Épisode** %02d", this.getFirstEpisode());
        } else if (amount == 2) {
            return String.format("**Épisodes** %02d et %02d", this.getFirstEpisode(), this.getLastEpisode());
        } else {
            return String.format("**Épisodes** %02d à %02d", this.getFirstEpisode(), this.getLastEpisode());
        }
    }

    default boolean isColliding(AnimeNightMeta meta) {

        boolean isSameStart = this.getStartDateTime().isEqual(meta.getStartDateTime());
        boolean isSameEnd   = this.getEndDateTime().isEqual(meta.getEndDateTime());

        boolean isStartDuring = this.getStartDateTime().isAfter(meta.getStartDateTime()) &&
                                this.getStartDateTime().isBefore(meta.getEndDateTime());

        boolean isEndDuring = this.getEndDateTime().isAfter(meta.getStartDateTime()) &&
                              this.getEndDateTime().isBefore(meta.getEndDateTime());

        boolean isOnTop = this.getStartDateTime().isBefore(meta.getStartDateTime()) &&
                          this.getEndDateTime().isAfter(meta.getEndDateTime());

        return isSameStart || isSameEnd || isStartDuring || isEndDuring || isOnTop;
    }

}
