package fr.anisekai.web.api.dto;

import fr.anisekai.server.entities.Track;
import fr.anisekai.wireless.api.media.enums.Codec;
import fr.anisekai.wireless.api.media.enums.CodecType;
import fr.anisekai.wireless.api.media.enums.Disposition;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public record TrackDto(
        long id,
        String name,
        Codec codec,
        CodecType type,
        @Nullable String language,
        Collection<Disposition> dispositions
) {

    public static TrackDto of(Track track) {

        return new TrackDto(
                track.getId(),
                track.getName(),
                track.getCodec(),
                track.getCodec().getType(),
                track.getLanguage(),
                Disposition.fromBits(track.getDispositions())
        );
    }

}
