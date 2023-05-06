package me.anisekai.toshiko.listeners;

import me.anisekai.toshiko.services.AnimeNightService;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ScheduledEventListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledEventListener.class);

    private final AnimeNightService service;

    public ScheduledEventListener(AnimeNightService service) {

        this.service = service;
    }

    @Override
    public void onScheduledEventDelete(ScheduledEventDeleteEvent event) {

        LOGGER.info("onScheduledEventDelete: Event {}", event.getScheduledEvent().getIdLong());
        this.service.cancelEvent(event.getScheduledEvent());
    }

    @Override
    public void onScheduledEventUpdateStatus(ScheduledEventUpdateStatusEvent event) {

        LOGGER.info(
                "onScheduledEventUpdateStatus: Event {} - {} > {}",
                event.getScheduledEvent().getIdLong(),
                event.getOldStatus().name(),
                event.getNewStatus().name()
        );

        switch (event.getNewStatus()) {
            case ACTIVE -> this.service.openEvent(event.getScheduledEvent());
            case COMPLETED -> this.service.closeEvent(event.getScheduledEvent());
            case CANCELED -> this.service.cancelEvent(event.getScheduledEvent());
        }
    }

}
