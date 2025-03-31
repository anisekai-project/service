package me.anisekai.server.events;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.server.entities.Setting;

public class SettingUpdatedEvent<V> extends EntityUpdatedEvent<Setting, V> {

    public SettingUpdatedEvent(Object source, Setting entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
