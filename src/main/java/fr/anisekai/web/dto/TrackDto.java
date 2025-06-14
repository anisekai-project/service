package fr.anisekai.web.dto;

import fr.anisekai.server.entities.Track;
import fr.anisekai.wireless.api.media.enums.Codec;
import fr.anisekai.wireless.api.media.enums.CodecType;
import org.json.JSONObject;

public class TrackDto {

    public  long   id;
    private Codec  codec;
    public  String name;
    public  String label;
    public  String language;

    public TrackDto(Track track) {

        this.id       = track.getId();
        this.codec    = track.getCodec();
        this.name     = track.getName();
        this.label    = track.getLabel();
        this.language = track.getLanguage();
    }

    public JSONObject toJson() {

        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("codec", this.codec.name());
        json.put("type", this.codec.getType().name());
        json.put("name", this.name);
        json.put("label", this.label);
        json.put("language", this.language);
        return json;
    }

}
