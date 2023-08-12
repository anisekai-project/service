package me.anisekai.toshiko;

import me.anisekai.toshiko.events.EntityCreatedEvent;
import me.anisekai.toshiko.events.EntityUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class DebugListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(DebugListener.class);

    @EventListener
    private void logCreate(EntityCreatedEvent<?> event) {

        LOGGER.debug(
                "  |  {} @ {} ({})",
                event.getClass().getSimpleName(),
                event.getEntity().getClass().getSimpleName(),
                event.getEntity().getId()
        );
    }

    @EventListener
    private void logChange(EntityUpdatedEvent<?, ?> event) {

        LOGGER.debug(
                "  |  {} @ {} ({}: {} -> {})",
                event.getClass().getSimpleName(),
                event.getEntity().getClass().getSimpleName(),
                event.getEntity().getId(),
                event.getPrevious(),
                event.getCurrent()
        );
    }

}
