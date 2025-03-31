package me.anisekai.api.plannifier.data;

import me.anisekai.api.plannifier.interfaces.ScheduleSpotData;

import java.time.ZonedDateTime;

public class TestSpot implements ScheduleSpotData<TestWatchTarget> {

    private final TestWatchTarget target;
    private final ZonedDateTime   startingAt;
    private final long            episodeCount;
    private final boolean         skipEnabled;

    public TestSpot(TestWatchTarget target, ZonedDateTime startingAt, long episodeCount) {

        this.target       = target;
        this.startingAt   = startingAt;
        this.episodeCount = episodeCount;
        this.skipEnabled  = true;
    }

    @Override
    public TestWatchTarget getWatchTarget() {

        return this.target;
    }

    @Override
    public ZonedDateTime getStartingAt() {

        return this.startingAt;
    }

    @Override
    public void setStartingAt(ZonedDateTime time) {

    }

    @Override
    public long getEpisodeCount() {

        return this.episodeCount;
    }

    @Override
    public void setEpisodeCount(long episodeCount) {

    }

    @Override
    public boolean isSkipEnabled() {

        return this.skipEnabled;
    }

    @Override
    public void setSkipEnabled(boolean skipEnabled) {

    }

}
