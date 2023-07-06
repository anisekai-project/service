package me.anisekai.toshiko.data;

import com.sun.syndication.feed.synd.SyndEntry;
import org.jdom.Element;

public class NyaaRssItem {

    private final String title;
    private final String link;
    private final String torrent;
    private       String infoHash;

    public NyaaRssItem(SyndEntry entry) {

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
