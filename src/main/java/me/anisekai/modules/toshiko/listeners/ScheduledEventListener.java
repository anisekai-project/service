package me.anisekai.modules.toshiko.listeners;

import me.anisekai.api.plannifier.data.CalibrationResult;
import me.anisekai.modules.shizue.interfaces.entities.IBroadcast;
import me.anisekai.modules.shizue.services.data.BroadcastDataService;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class ScheduledEventListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledEventListener.class);

    private final BroadcastDataService service;

    public ScheduledEventListener(BroadcastDataService service) {

        this.service = service;
    }

    private void cancel(ScheduledEvent event) {

        LOGGER.info("onScheduledEventDelete: Event {}", event.getIdLong());
        IBroadcast        cancelled = this.service.cancel(event);
        CalibrationResult calibrate = this.service.createScheduler().calibrate();

        String report = String.format(
                """
                              ```
                              \s
                              ———————————【 Event Cancel Report 】———————————
                        
                                 Entity  %s
                                  Event  %s
                                  Anime  %s
                                Episode  %s → %s
                        
                              ———————【 Schedule Calibration Report 】———————
                        
                                Updated  %s
                                Deleted  %s
                              \s
                              ```
                        """,
                cancelled.getId(),
                cancelled.getEventId(),
                cancelled.getWatchTarget().getName(),
                cancelled.getFirstEpisode(),
                cancelled.getFirstEpisode() + cancelled.getEpisodeCount() - 1,
                calibrate.getUpdateCount(),
                calibrate.getDeleteCount()
        );

        LOGGER.debug(Base64.getEncoder().encodeToString(report.getBytes()));

        // TODO: Send the report on the admin channel
    }

    @Override
    public void onScheduledEventDelete(ScheduledEventDeleteEvent event) {

        this.cancel(event.getScheduledEvent());
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
                this.cancel(event.getScheduledEvent());
            }
        }
    }

}
