package me.anisekai.toshiko.events.animenight;

import me.anisekai.toshiko.entities.AnimeNight;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.springframework.context.ApplicationEvent;

public class AnimeNightStartedEvent extends ApplicationEvent {

    private final AnimeNight     animeNight;
    private final ScheduledEvent scheduledEvent;

    public AnimeNightStartedEvent(Object source, AnimeNight animeNight, ScheduledEvent scheduledEvent) {

        super(source);
        this.animeNight     = animeNight;
        this.scheduledEvent = scheduledEvent;
    }

    public AnimeNight getAnimeNight() {

        return this.animeNight;
    }

    public ScheduledEvent getScheduledEvent() {

        return this.scheduledEvent;
    }

}
