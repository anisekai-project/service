package me.anisekai.api.persistence;

import me.anisekai.api.persistence.data.ITestEntity;
import me.anisekai.api.persistence.data.TestEntity;
import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayName("Proxy")
@Tag("slow-test")
public class ProxyTests {

    private static final String TEST_VAL_STR_1 = "unit test";
    private static final String TEST_VAL_STR_2 = "lorem ipsum";

    @Test
    @DisplayName("Persistence | Update entity | null -> something")
    public void testEventProxyNullToSomething() {

        TestEntity entity = new TestEntity();

        EventProxy<ITestEntity, TestEntity> eventProxy = Assertions.assertDoesNotThrow(() -> new EventProxy<>(entity));
        ITestEntity                         proxy      = eventProxy.startProxy();

        proxy.setName(TEST_VAL_STR_1);

        List<EntityUpdatedEvent<TestEntity, ?>> events = eventProxy.getEvents();

        Assertions.assertEquals(1, events.size(), "No event or too much events have been generated.");
        EntityUpdatedEvent<TestEntity, ?> event = events.get(0);

        Assertions.assertNull(event.getPrevious(), "Wrong previous value in event");
        Assertions.assertEquals(TEST_VAL_STR_1, event.getCurrent(), "Wrong current value in event");
        Assertions.assertEquals(TEST_VAL_STR_1, proxy.getName(), "Proxy getter not redirected");
        Assertions.assertEquals(TEST_VAL_STR_1, entity.getName(), "Proxy did not edit entity");
    }

    @Test
    @DisplayName("Persistence | Update entity | something -> null")
    public void testEventProxySomethingToNull() {

        TestEntity entity = new TestEntity();
        entity.setName(TEST_VAL_STR_1);

        EventProxy<ITestEntity, TestEntity> eventProxy = Assertions.assertDoesNotThrow(() -> new EventProxy<>(entity));
        ITestEntity                         proxy      = eventProxy.startProxy();

        proxy.setName(null);

        List<EntityUpdatedEvent<TestEntity, ?>> events = eventProxy.getEvents();

        Assertions.assertEquals(1, events.size(), "No event or too much events have been generated.");
        EntityUpdatedEvent<TestEntity, ?> event = events.get(0);

        Assertions.assertEquals(TEST_VAL_STR_1, event.getPrevious(), "Wrong previous value in event");
        Assertions.assertNull(event.getCurrent(), "Wrong current value in event");
        Assertions.assertNull(proxy.getName(), "Proxy getter not redirected");
        Assertions.assertNull(entity.getName(), "Proxy did not edit entity");
    }

    @Test
    @DisplayName("Persistence | Update entity | something -> something")
    public void testEventProxySomethingToSomething() {

        TestEntity entity = new TestEntity();
        entity.setName(TEST_VAL_STR_1);

        EventProxy<ITestEntity, TestEntity> eventProxy = Assertions.assertDoesNotThrow(() -> new EventProxy<>(entity));
        ITestEntity                         proxy      = eventProxy.startProxy();

        proxy.setName(TEST_VAL_STR_2);

        List<EntityUpdatedEvent<TestEntity, ?>> events = eventProxy.getEvents();

        Assertions.assertEquals(1, events.size(), "No event or too much events have been generated.");
        EntityUpdatedEvent<TestEntity, ?> event = events.get(0);

        Assertions.assertEquals(TEST_VAL_STR_1, event.getPrevious(), "Wrong previous value in event");
        Assertions.assertEquals(TEST_VAL_STR_2, event.getCurrent(), "Wrong current value in event");
        Assertions.assertEquals(TEST_VAL_STR_2, proxy.getName(), "Proxy getter not redirected");
        Assertions.assertEquals(TEST_VAL_STR_2, entity.getName(), "Proxy did not edit entity");
    }

    @Test
    @DisplayName("Persistence | Update entity | no changes")
    public void testEventProxyNoChanges() {

        TestEntity entity = new TestEntity();
        entity.setName(TEST_VAL_STR_1);

        EventProxy<ITestEntity, TestEntity> eventProxy = Assertions.assertDoesNotThrow(() -> new EventProxy<>(entity));
        ITestEntity                         proxy      = eventProxy.startProxy();

        proxy.setName(TEST_VAL_STR_1);

        Assertions.assertEquals(0, eventProxy.getEvents().size(), "No event should have been generated.");
    }

    @Test
    @DisplayName("Persistence | Update entity | rollback")
    public void testEventProxyRollback() {

        TestEntity entity = new TestEntity();
        entity.setName(TEST_VAL_STR_1);

        EventProxy<ITestEntity, TestEntity> eventProxy = Assertions.assertDoesNotThrow(() -> new EventProxy<>(entity));
        ITestEntity                         proxy      = eventProxy.startProxy();

        proxy.setName(TEST_VAL_STR_2);
        proxy.setName(TEST_VAL_STR_1);

        List<EntityUpdatedEvent<TestEntity, ?>> events = eventProxy.getEvents();
        Assertions.assertEquals(0, eventProxy.getEvents().size(), "No event should have been generated.");
    }

}
