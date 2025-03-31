package me.anisekai.api.transmission;

import com.sun.syndication.feed.synd.SyndEntry;
import org.jdom.Element;

public class NyaaRssEntry {

    private final String title;
    private final String link;
    private final String torrent;
    private       String infoHash;

    public NyaaRssEntry(SyndEntry entry) {

        this.title   = entry.getTitle();
        this.torrent = entry.getLink();
        this.link    = entry.getUri();

        for (Element tag : (Iterable<Element>) entry.getForeignMarkup()) {
            if (tag.getName().equals("infoHash")) {
                this.infoHash = tag.getText();
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

    public String getInfoHash() {

        return this.infoHash;
    }

}
