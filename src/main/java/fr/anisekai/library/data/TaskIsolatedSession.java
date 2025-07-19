package fr.anisekai.library.data;

import fr.anisekai.sanctum.interfaces.isolation.IsolationSession;
import fr.anisekai.web.data.SessionToken;
import fr.anisekai.wireless.remote.interfaces.TaskEntity;

public class TaskIsolatedSession {

    private final TaskEntity       entity;
    private final IsolationSession session;
    private       SessionToken     token;

    public TaskIsolatedSession(TaskEntity entity, IsolationSession session) {

        this.entity  = entity;
        this.session = session;
        this.token   = null;
    }

    public TaskEntity getEntity() {

        return this.entity;
    }

    public IsolationSession getSession() {

        return this.session;
    }

    public boolean isClaimed() {

        return this.token != null;
    }

    public SessionToken getClaim() {

        return this.token;
    }

    public void claim(SessionToken token) {

        this.token = token;
    }

}
