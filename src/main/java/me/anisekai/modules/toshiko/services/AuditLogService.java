package me.anisekai.modules.toshiko.services;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import jakarta.annotation.Priority;
import me.anisekai.globals.tasking.TaskingService;
import me.anisekai.modules.toshiko.tasking.factories.MessageLogTaskFactory;
import me.anisekai.modules.freya.enums.TorrentStatus;
import me.anisekai.modules.freya.events.torrent.TorrentStatusUpdatedEvent;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final TaskingService service;

    public AuditLogService(TaskingService service) {

        this.service = service;
    }

    // @EventListener
    @Priority(Integer.MIN_VALUE)
    public void onAuditableEvent(DiscordEmbeddable embeddable) {

        // Special check to avoid spammy behaviour
        if (embeddable instanceof TorrentStatusUpdatedEvent event) {
            if (event.getPrevious() == TorrentStatus.SEEDING && event.getCurrent() == TorrentStatus.STOPPED) {
                // The file isn't available for seeding anymore, no need to log this
                return;
            }
        }

        MessageLogTaskFactory.queue(this.service, embeddable.asEmbed().build());
    }

}
