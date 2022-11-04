package me.anisekai.toshiko.listeners;

import me.anisekai.toshiko.entities.AnimeNight;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateEndTimeEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStartTimeEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class ScheduledEventListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledEventListener.class);

    private final ToshikoService service;

    public ScheduledEventListener(ToshikoService service) {

        this.service = service;
    }

    @Override
    public void onScheduledEventUpdateStartTime(ScheduledEventUpdateStartTimeEvent event) {

        this.service.updateEvent(event.getScheduledEvent(), night -> {
            night.setStartTime(event.getScheduledEvent().getStartTime());
        });
    }

    @Override
    public void onScheduledEventUpdateEndTime(ScheduledEventUpdateEndTimeEvent event) {

        ScheduledEvent scheduledEvent = event.getScheduledEvent();
        if (scheduledEvent.getEndTime() != null) {
            this.service.updateEvent(event.getScheduledEvent(), night -> {
                night.setEndTime(scheduledEvent.getEndTime());
            });
        }
    }

    @Override
    public void onScheduledEventDelete(ScheduledEventDeleteEvent event) {
        LOGGER.info("ScheduledEvent {} deleted.", event.getScheduledEvent().getId());

        this.service.updateEvent(event.getScheduledEvent(), night -> {
            LOGGER.info(" > Cancelling event...");
            night.setStatus(ScheduledEvent.Status.CANCELED);
        }).ifPresent(night -> {
            LOGGER.info(" > Doing schedule recalibration...");
            this.service.recalibrateSchedule(night.getAnime());
        });
    }

    @Override
    public void onScheduledEventUpdateStatus(ScheduledEventUpdateStatusEvent event) {

        LOGGER.info("ScheduledEvent {} status change: {} -> {}", event.getScheduledEvent().getId(), event.getOldStatus(), event.getNewStatus());

        switch (event.getNewStatus()) {
            case ACTIVE -> this.service.startEvent(event.getScheduledEvent());
            case COMPLETED -> this.service.closeEvent(event.getScheduledEvent());
        };
    }

}
