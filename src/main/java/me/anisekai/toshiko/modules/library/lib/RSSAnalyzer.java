package me.anisekai.toshiko.modules.library.lib;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.springframework.util.function.ThrowingFunction;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class RSSAnalyzer<T> {

    private final URI                            uri;
    private final ThrowingFunction<SyndEntry, T> converter;

    public RSSAnalyzer(URI uri, ThrowingFunction<SyndEntry, T> converter) {

        this.uri       = uri;
        this.converter = converter;
    }

    public List<T> analyze() throws Exception {

        SyndFeedInput       input   = new SyndFeedInput();
        SyndFeed            feed    = input.build(new XmlReader(this.uri.toURL()));
        Iterable<SyndEntry> entries = (List<SyndEntry>) feed.getEntries();

        List<T> items = new ArrayList<>();

        for (SyndEntry entry : entries) {
            items.add(this.converter.applyWithException(entry));
        }

        return items;
    }

}
