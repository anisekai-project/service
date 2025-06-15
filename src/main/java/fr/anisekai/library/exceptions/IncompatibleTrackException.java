package fr.anisekai.library.exceptions;

import fr.anisekai.wireless.remote.interfaces.TrackEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncompatibleTrackException extends RuntimeException {

    private final TrackEntity<?> track;

    public IncompatibleTrackException(String message, TrackEntity<?> track) {

        super(message);
        this.track = track;
    }

    public TrackEntity<?> getTrack() {

        return this.track;
    }

}
