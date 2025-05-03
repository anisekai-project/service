package me.anisekai.server.events;

import me.anisekai.server.entities.Setting;

public class SettingUpdatedEvent<V> extends EntityUpdatedEventAdapter<Setting, V> {

    public SettingUpdatedEvent(Object source, Setting entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
