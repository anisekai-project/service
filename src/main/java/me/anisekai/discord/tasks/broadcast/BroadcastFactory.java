package me.anisekai.discord.tasks.broadcast;

import fr.anisekai.wireless.remote.interfaces.BroadcastEntity;
import me.anisekai.discord.JDAStore;
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

    @Override
    public boolean hasNamedTask() {

        return true;
    }

    @Override
    public boolean allowDuplicated() {

        return false;
    }

    public String asTaskName(BroadcastEntity<?> broadcast) {

        return String.format("%s:%s", this.getName(), broadcast.getId());
    }

}
