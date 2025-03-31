package me.anisekai.discord.tasks.broadcast;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.discord.JDAStore;
import me.anisekai.server.entities.Task;
import me.anisekai.server.interfaces.IBroadcast;
import me.anisekai.server.services.BroadcastService;
import me.anisekai.server.services.TaskService;
import me.anisekai.server.tasking.TaskExecutor;
import me.anisekai.server.tasking.TaskFactory;

public abstract class BroadcastFactory<T extends TaskExecutor> implements TaskFactory<T> {

    public static final String PREFIX = "broadcast";

    private final TaskService      service;
    private final JDAStore         store;
    private final BroadcastService broadcastService;

    public BroadcastFactory(TaskService service, JDAStore store, BroadcastService broadcastService) {

        this.service          = service;
        this.store            = store;
        this.broadcastService = broadcastService;
    }

    public String getPrefix() {

        return PREFIX;
    }

    public TaskService getService() {

        return this.service;
    }

    public BroadcastService getBroadcastService() {

        return this.broadcastService;
    }

    public JDAStore getStore() {

        return this.store;
    }

    /**
     * Check if this {@link TaskFactory} has named task. Named task usually mean that each {@link TaskExecutor} created
     * is associated to a specific {@link IEntity}.
     *
     * @return True if this {@link TaskFactory} handles named task, false otherwise.
     */
    @Override
    public boolean hasNamedTask() {

        return true;
    }

    /**
     * Check if this {@link TaskFactory} allows multiple {@link Task} with the same {@link Task#getName()}.
     *
     * @return True if duplicates are allowed, false otherwise.
     */
    @Override
    public boolean allowDuplicated() {

        return false;
    }

    public String asTaskName(IBroadcast<?> broadcast) {

        return String.format("%s:%s", this.getName(), broadcast.getId());
    }

}
