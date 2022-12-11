package me.anisekai.toshiko.listeners;

import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStartTimeEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ScheduledEventListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledEventListener.class);

    private final ToshikoService service;

    public ScheduledEventListener(ToshikoService service) {

        this.service = service;
    }

    @Override
    public void onScheduledEventUpdateStartTime(ScheduledEventUpdateStartTimeEvent event) {

        this.service.findAnimeNight(event.getScheduledEvent()).ifPresent(night -> {
            night.setStartDateTime(event.getScheduledEvent().getStartTime());
            this.service.getAnimeNightRepository().save(night);
        });
    }

    @Override
    public void onScheduledEventDelete(ScheduledEventDeleteEvent event) {

        this.service.cancelEvent(event.getScheduledEvent());
    }

    @Override
    public void onScheduledEventUpdateStatus(ScheduledEventUpdateStatusEvent event) {

        switch (event.getNewStatus()) {
            case ACTIVE -> this.service.startEvent(event.getScheduledEvent());
            case COMPLETED -> this.service.closeEvent(event.getScheduledEvent());
            case CANCELED -> this.service.cancelEvent(event.getScheduledEvent());
        }
    }

}
