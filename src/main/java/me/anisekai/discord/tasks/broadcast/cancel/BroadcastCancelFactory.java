package me.anisekai.discord.tasks.broadcast.cancel;

import jakarta.annotation.PostConstruct;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.discord.JDAStore;
import me.anisekai.discord.tasks.broadcast.BroadcastFactory;
import me.anisekai.discord.tasks.broadcast.BroadcastTask;
import me.anisekai.server.entities.Task;
import me.anisekai.server.interfaces.IBroadcast;
import me.anisekai.server.services.BroadcastService;
import me.anisekai.server.services.TaskService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.server.tasking.TaskFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class BroadcastCancelFactory extends BroadcastFactory<BroadcastCancelTask> {

    private static final String NAME = "cancel";

    public BroadcastCancelFactory(TaskService service, JDAStore store, BroadcastService broadcastService) {

        super(service, store, broadcastService);
    }

    /**
     * Get this {@link TaskFactory} name, which will be used to associate it with a {@link Task}.
     *
     * @return The {@link TaskFactory} name.
     */
    @Override
    public @NotNull String getName() {

        return String.format("%s:%s", this.getPrefix(), NAME);
    }

    /**
     * Create an instance of {@link TaskExecutor}. This is useful if your task has some bean dependencies.
     *
     * @return A new {@link TaskExecutor} instance.
     */
    @Override
    public @NotNull BroadcastCancelTask create() {

        return new BroadcastCancelTask(this.getStore(), this.getBroadcastService());
    }

    public Task queue(IBroadcast<?> broadcast) {

        return this.queue(broadcast, Task.PRIORITY_AUTOMATIC_LOW);
    }

    public Task queue(IBroadcast<?> broadcast, long priority) {

        String        name      = String.format("%s:%s", this.getName(), broadcast.getId());
        BookshelfJson arguments = new BookshelfJson();
        arguments.put(BroadcastTask.OPT_BROADCAST, broadcast.getId());

        return this.getService().queue(this, name, arguments, priority);
    }

    @PostConstruct
    public void postConstruct() {

        this.getService().registerFactory(this);
    }

}
