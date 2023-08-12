package me.anisekai.toshiko.modules.discord.services;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import jakarta.annotation.Priority;
import me.anisekai.toshiko.modules.discord.JdaStore;
import me.anisekai.toshiko.services.TaskService;
import me.anisekai.toshiko.modules.discord.tasks.SendMessageTask;
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

        TextChannel auditLogChannel = this.store.getAuditLogChannel();
        this.service.queue(new SendMessageTask(auditLogChannel, embeddable));
    }

}
