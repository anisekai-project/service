package fr.anisekai.server.entities.adapters;

import fr.anisekai.server.events.torrent.TorrentStatusUpdatedEvent;
import fr.anisekai.wireless.api.persistence.TriggerEvent;
import fr.anisekai.wireless.api.services.Transmission;
import fr.anisekai.wireless.remote.interfaces.TaskEntity;
import fr.anisekai.wireless.remote.interfaces.TorrentEntity;
import org.jetbrains.annotations.NotNull;

public interface TorrentEventAdapter extends TorrentEntity {

    @Override
    @TriggerEvent(TorrentStatusUpdatedEvent.class)
    void setStatus(Transmission.@NotNull TorrentStatus status);

    /**
     * Retrieve this {@link TorrentEntity}'s priority. This directly affect underlying {@link TaskEntity} created
     * through this {@link TorrentEntity}.
     *
     * @return A priority level.
     */
    byte getPriority();

    /**
     * Define this {@link TorrentEntity}'s priority. This directly affect underlying {@link TaskEntity} created through
     * this {@link TorrentEntity}.
     *
     * @param priority
     *         A priority level.
     */
    void setPriority(byte priority);

}
