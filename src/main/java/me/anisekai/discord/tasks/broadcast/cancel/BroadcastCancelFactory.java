package me.anisekai.discord.tasks.broadcast.cancel;

import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.remote.interfaces.BroadcastEntity;
import jakarta.annotation.PostConstruct;
import me.anisekai.discord.JDAStore;
import me.anisekai.discord.tasks.broadcast.BroadcastFactory;
import me.anisekai.discord.tasks.broadcast.BroadcastTask;
import me.anisekai.server.entities.Task;
import me.anisekai.server.services.BroadcastService;
import me.anisekai.server.services.TaskService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class BroadcastCancelFactory extends BroadcastFactory<BroadcastCancelTask> {

    private static final String NAME = "cancel";

    public BroadcastCancelFactory(TaskService service, JDAStore store, BroadcastService broadcastService) {

        super(service, store, broadcastService);
    }

    @Override
    public @NotNull String getName() {

        return String.format("%s:%s", this.getPrefix(), NAME);
    }

    @Override
    public @NotNull BroadcastCancelTask create() {

        return new BroadcastCancelTask(this.getStore(), this.getBroadcastService());
    }

    public Task queue(BroadcastEntity<?> broadcast) {

        return this.queue(broadcast, Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(BroadcastEntity<?> broadcast, byte priority) {

        String       name      = String.format("%s:%s", this.getName(), broadcast.getId());
        AnisekaiJson arguments = new AnisekaiJson();
        arguments.put(BroadcastTask.OPT_BROADCAST, broadcast.getId());

        return this.getService().queue(this, name, arguments, priority);
    }

    @PostConstruct
    public void postConstruct() {

        this.getService().registerFactory(this);
    }

}
