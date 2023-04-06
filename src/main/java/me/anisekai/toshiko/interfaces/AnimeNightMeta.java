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

    default boolean isColliding(OffsetDateTime startTime, OffsetDateTime endTime) {

        boolean isSameStart = this.getStartDateTime().isEqual(startTime);
        boolean isSameEnd   = this.getEndDateTime().isEqual(endTime);

        boolean isStartDuring = this.getStartDateTime().isAfter(startTime) &&
                                this.getStartDateTime().isBefore(endTime);

        boolean isEndDuring = this.getEndDateTime().isAfter(startTime) &&
                              this.getEndDateTime().isBefore(endTime);

        boolean isOnTop = this.getStartDateTime().isBefore(startTime) &&
                          this.getEndDateTime().isAfter(endTime);

        return isSameStart || isSameEnd || isStartDuring || isEndDuring || isOnTop;
    }

    default boolean isColliding(AnimeNightMeta meta) {

        return this.isColliding(meta.getStartDateTime(), meta.getEndDateTime());
    }

}
