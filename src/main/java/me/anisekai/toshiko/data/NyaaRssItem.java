package me.anisekai.toshiko.data;

import com.sun.syndication.feed.synd.SyndEntry;
import org.jdom.Element;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class NyaaRssItem {

    private final String         title;
    private final String         link;
    private final String         torrent;
    private final OffsetDateTime published;
    private       String         infoHash;
    private       String         size;

    public NyaaRssItem(SyndEntry entry) {

        this.title     = entry.getTitle();
        this.torrent   = entry.getLink();
        this.link      = entry.getUri();
        this.published = entry.getPublishedDate().toInstant().atOffset(ZoneOffset.UTC);

        for (Element tag : (Iterable<Element>) entry.getForeignMarkup()) {
            switch (tag.getName()) {
                case "infoHash" -> this.infoHash = tag.getText();
                case "size" -> this.size = tag.getText();
            }
        }
    }

    public String getTitle() {

        return this.title;
    }

    public String getLink() {

        return this.link;
    }

    public String getTorrent() {

        return this.torrent;
    }

    public OffsetDateTime getPublished() {

        return this.published;
    }

    public String getInfoHash() {

        return this.infoHash;
    }

    public void setInfoHash(String infoHash) {

        this.infoHash = infoHash;
    }

    public String getSize() {

        return this.size;
    }

    public void setSize(String size) {

        this.size = size;
    }
}
