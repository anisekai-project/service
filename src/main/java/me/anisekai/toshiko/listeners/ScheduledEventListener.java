package me.anisekai.toshiko.listeners;

import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class ScheduledEventListener extends ListenerAdapter {

    private final ToshikoService service;

    public ScheduledEventListener(ToshikoService service) {

        this.service = service;
    }

    @Override
    public void onScheduledEventUpdateStatus(ScheduledEventUpdateStatusEvent event) {

        switch (event.getNewStatus()) {
            case ACTIVE -> this.service.startEvent(event.getScheduledEvent());
            case COMPLETED -> this.service.closeEvent(event.getScheduledEvent());
        }
    }

}
