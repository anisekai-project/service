package me.anisekai.api.mkv;

import me.anisekai.api.json.BookshelfJson;

public final class MediaTrack {

    private final int            id;
    private final String         codec;
    private final MediaFormat    format;
    private final MediaTrackType type;
    private final String         trackName;
    private final String         language;
    private final String         languageIETF;
    private final boolean        defaultTrack;

    public MediaTrack(BookshelfJson json) {

        this.id           = json.getInt("id");
        this.codec        = json.getString("codec");
        this.type         = MediaTrackType.valueOf(json.getString("type").toUpperCase());
        this.trackName    = json.optString("properties.track_name");
        this.language     = json.getString("properties.language");
        this.languageIETF = json.getString("properties.language_ietf");
        this.defaultTrack = json.getBoolean("properties.default_track");

        this.format = MediaFormat.fromCodec(this.codec);
    }

    public int getId() {

        return this.id;
    }

    public String asFileName(String basename) {

        return this.asFileName(basename, this.getFormat());
    }

    public String asFileName(String basename, MediaFormat formatOverride) {

        return String.format(
                "%s.%s.%s.%s.%s",
                basename,
                this.getId(),
                this.getType().name().toLowerCase(),
                this.getLanguageIETF(),
                formatOverride.getExtension()
        );
    }


    public String getCodec() {

        return this.codec;
    }

    public MediaFormat getFormat() {

        return this.format;
    }

    public MediaTrackType getType() {

        return this.type;
    }

    public String getTrackName() {

        return this.trackName;
    }

    public String getLanguage() {

        return this.language;
    }

    public String getLanguageIETF() {

        return this.languageIETF;
    }

    public boolean isDefaultTrack() {

        return this.defaultTrack;
    }

}
