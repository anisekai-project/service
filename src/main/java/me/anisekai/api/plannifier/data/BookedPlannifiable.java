package me.anisekai.api.plannifier.data;

import me.anisekai.api.plannifier.interfaces.Plannifiable;
import me.anisekai.api.plannifier.interfaces.ScheduleSpotData;
import me.anisekai.api.plannifier.interfaces.WatchTarget;

import java.time.ZonedDateTime;

public class BookedPlannifiable<T extends WatchTarget> implements Plannifiable<T> {

    private final T             target;
    private       ZonedDateTime startingAt;
    private       long          firstEpisode;
    private       long          episodeCount;
    private       boolean       skipEnabled;

    public BookedPlannifiable(ScheduleSpotData<T> scheduleSpotData) {

        this(scheduleSpotData, scheduleSpotData.getWatchTarget().getEpisodeWatched() + 1);
    }

    public BookedPlannifiable(ScheduleSpotData<T> scheduleSpotData, long firstEpisode) {

        this.target       = scheduleSpotData.getWatchTarget();
        this.startingAt   = scheduleSpotData.getStartingAt();
        this.firstEpisode = firstEpisode;
        this.episodeCount = scheduleSpotData.getEpisodeCount();
        this.skipEnabled  = scheduleSpotData.isSkipEnabled();
    }

    @Override
    public long getFirstEpisode() {

        return this.firstEpisode;
    }

    @Override
    public void setFirstEpisode(long firstEpisode) {

        this.firstEpisode = firstEpisode;
    }

    @Override
    public T getWatchTarget() {

        return this.target;
    }

    @Override
    public ZonedDateTime getStartingAt() {

        return this.startingAt;
    }

    @Override
    public void setStartingAt(ZonedDateTime time) {

        this.startingAt = time;
    }

    @Override
    public long getEpisodeCount() {

        return this.episodeCount;
    }

    @Override
    public void setEpisodeCount(long episodeCount) {

        this.episodeCount = episodeCount;
    }

    @Override
    public boolean isSkipEnabled() {

        return this.skipEnabled;
    }

    @Override
    public void setSkipEnabled(boolean skipEnabled) {

        this.skipEnabled = skipEnabled;
    }

}
