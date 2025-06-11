package fr.anisekai.server.entities.adapters;

import fr.anisekai.wireless.remote.interfaces.TaskEntity;

public interface TaskEventAdapter extends TaskEntity {

    default byte failure() {

        this.setFailureCount((byte) (this.getFailureCount() + 1));
        return this.getFailureCount();
    }

}
