package me.anisekai.toshiko.modules.discord.listeners;

import me.anisekai.toshiko.interfaces.entities.IBroadcast;
import me.anisekai.toshiko.services.data.BroadcastDataService;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ScheduledEventListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledEventListener.class);

    private final BroadcastDataService service;

    public ScheduledEventListener(BroadcastDataService service) {

        this.service = service;
    }

    @Override
    public void onScheduledEventDelete(ScheduledEventDeleteEvent event) {

        LOGGER.info("onScheduledEventDelete: Event {}", event.getScheduledEvent().getIdLong());
        IBroadcast cancelled = this.service.cancel(event.getScheduledEvent());
        this.service.calibrate(cancelled.getAnime());
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
            case ACTIVE -> this.service.open(event.getScheduledEvent());
            case COMPLETED -> this.service.close(event.getScheduledEvent());
            case CANCELED -> {
                IBroadcast cancelled = this.service.cancel(event.getScheduledEvent());
                this.service.calibrate(cancelled.getAnime());
            }
        }
    }

}
