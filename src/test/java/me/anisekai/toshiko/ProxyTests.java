package me.anisekai.toshiko;

import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.api.persistence.events.EntityUpdatedEvent;
import me.anisekai.api.persistence.EventProxy;
import me.anisekai.modules.linn.interfaces.IAnime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayName("Proxy")
@Tag("slow-test")
public class ProxyTests {

    @Test
    @DisplayName("Test Successful Proxy")
    public void testProxyEntity() {

        Assertions.assertDoesNotThrow(() -> new EventProxy<>(new Anime()));
    }

    @Test
    @DisplayName("Proxy Event (null -> something)")
    public void testUpdateProxyNullToSomething() {

        String testValue = "test";
        Anime anime = new Anime();

        EventProxy<IAnime, Anime> eventProxy = new EventProxy<>(anime);
        IAnime                    proxy      = eventProxy.startProxy();

        proxy.setName(testValue);

        List<EntityUpdatedEvent<Anime, ?>> events = eventProxy.getEvents();

        Assertions.assertEquals(1, events.size());
        Assertions.assertNull(events.get(0).getPrevious());
        Assertions.assertEquals(testValue, events.get(0).getCurrent());
    }

    @Test
    @DisplayName("Proxy Event (something -> null)")
    public void testUpdateProxySomethingToNull() {

        String testValue = "test";
        Anime anime = new Anime();
        anime.setName(testValue);

        EventProxy<IAnime, Anime> eventProxy = new EventProxy<>(anime);
        IAnime                    proxy      = eventProxy.startProxy();

        proxy.setName(null);

        List<EntityUpdatedEvent<Anime, ?>> events = eventProxy.getEvents();

        Assertions.assertEquals(1, events.size());
        Assertions.assertNull(events.get(0).getCurrent());
        Assertions.assertEquals(testValue, events.get(0).getPrevious());
    }

    @Test
    @DisplayName("Proxy Event (something -> something)")
    public void testUpdateProxySomethingToSomething() {

        String testValue = "test";
        String nextValue = "unit";
        Anime anime = new Anime();
        anime.setName(testValue);

        EventProxy<IAnime, Anime> eventProxy = new EventProxy<>(anime);
        IAnime                    proxy      = eventProxy.startProxy();

        proxy.setName(nextValue);

        List<EntityUpdatedEvent<Anime, ?>> events = eventProxy.getEvents();

        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals(nextValue, events.get(0).getCurrent());
        Assertions.assertEquals(testValue, events.get(0).getPrevious());
    }

    @Test
    @DisplayName("Proxy Event (unchanged)")
    public void testUpdateProxyUnchanged() {

        String testValue = "test";
        Anime anime = new Anime();
        anime.setName(testValue);

        EventProxy<IAnime, Anime> eventProxy = new EventProxy<>(anime);
        IAnime                    proxy      = eventProxy.startProxy();

        proxy.setName(testValue);

        List<EntityUpdatedEvent<Anime, ?>> events = eventProxy.getEvents();

        Assertions.assertEquals(0, events.size());
    }

    @Test
    @DisplayName("Proxy Event (reverted)")
    public void testUpdateProxyReverted() {

        String testValue = "test";
        Anime anime = new Anime();
        anime.setName(testValue);

        EventProxy<IAnime, Anime> eventProxy = new EventProxy<>(anime);
        IAnime                    proxy      = eventProxy.startProxy();

        proxy.setName("hi");
        proxy.setName(testValue);

        List<EntityUpdatedEvent<Anime, ?>> events = eventProxy.getEvents();

        Assertions.assertEquals(0, events.size());
    }

    @Test
    @DisplayName("Proxy Original Updated")
    public void testOriginalUpdated() {

        Anime anime = new Anime();
        anime.setName("hello");

        EventProxy<IAnime, Anime> eventProxy = new EventProxy<>(anime);
        IAnime                    proxy      = eventProxy.startProxy();

        proxy.setName("world");

        Assertions.assertEquals("world", anime.getName());
    }
}
