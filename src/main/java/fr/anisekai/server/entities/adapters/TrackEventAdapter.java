package fr.anisekai.server.entities.adapters;

import fr.anisekai.server.entities.Episode;
import fr.anisekai.wireless.remote.interfaces.TrackEntity;

public interface TrackEventAdapter extends TrackEntity<Episode> {


    default String asFilename() {

        return String.format(
                "%s.%s",
                this.getId(),
                this.getCodec().getExtension()
        );
    }

}
