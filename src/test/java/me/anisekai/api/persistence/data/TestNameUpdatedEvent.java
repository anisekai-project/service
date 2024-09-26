package me.anisekai.api.persistence.data;

import me.anisekai.api.persistence.events.EntityUpdatedEvent;

public class TestNameUpdatedEvent extends EntityUpdatedEvent<TestEntity, String> {

    public TestNameUpdatedEvent(Object source, TestEntity entity, String previous, String current) {

        super(source, entity, previous, current);
    }

}
