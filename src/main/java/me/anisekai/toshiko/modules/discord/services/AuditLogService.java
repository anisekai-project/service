package me.anisekai.toshiko.modules.discord.services;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import jakarta.annotation.Priority;
import me.anisekai.toshiko.enums.TorrentStatus;
import me.anisekai.toshiko.events.torrent.TorrentStatusUpdatedEvent;
import me.anisekai.toshiko.modules.discord.JdaStore;
import me.anisekai.toshiko.modules.discord.tasks.SendMessageTask;
import me.anisekai.toshiko.services.TaskService;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final JdaStore    store;
    private final TaskService service;

    public AuditLogService(JdaStore store, TaskService service) {

        this.store   = store;
        this.service = service;
    }

    @EventListener
    @Priority(Integer.MIN_VALUE)
    public void onAuditableEvent(DiscordEmbeddable embeddable) {

        // Special check to avoid spammy behaviour
        if (embeddable instanceof TorrentStatusUpdatedEvent event) {
            if (event.getPrevious() == TorrentStatus.SEEDING && event.getCurrent() == TorrentStatus.STOPPED) {
                // The file isn't available for seeding anymore, no need to log this
                return;
            }
        }

        TextChannel auditLogChannel = this.store.getAuditLogChannel();
        this.service.queue(new SendMessageTask(auditLogChannel, embeddable));
    }

}
