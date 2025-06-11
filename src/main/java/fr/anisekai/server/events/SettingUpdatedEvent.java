package fr.anisekai.server.events;

import fr.anisekai.server.entities.Setting;

public class SettingUpdatedEvent<V> extends EntityUpdatedEventAdapter<Setting, V> {

    public SettingUpdatedEvent(Object source, Setting entity, V previous, V current) {

        super(source, entity, previous, current);
    }

}
