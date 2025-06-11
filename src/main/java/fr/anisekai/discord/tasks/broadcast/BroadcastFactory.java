package fr.anisekai.discord.tasks.broadcast;

import fr.anisekai.wireless.remote.interfaces.BroadcastEntity;
import fr.anisekai.discord.JDAStore;
import fr.anisekai.server.services.BroadcastService;
import fr.anisekai.server.services.TaskService;
import fr.anisekai.server.tasking.TaskExecutor;
import fr.anisekai.server.tasking.TaskFactory;

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
