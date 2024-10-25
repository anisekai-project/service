package me.anisekai.api.persistence.data;

import me.anisekai.api.persistence.IEntity;
import me.anisekai.api.persistence.TriggerEvent;

public interface ITestEntity extends IEntity<Long> {

    String getName();

    @TriggerEvent(TestNameUpdatedEvent.class)
    void setName(String name);

}
