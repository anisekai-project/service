package me.anisekai.api.nyaa;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.springframework.util.function.ThrowingFunction;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public final class NyaaFeed {

    private NyaaFeed() {}

    public static <T> List<T> analyze(URI uri, ThrowingFunction<SyndEntry, T> converter) throws Exception {

        SyndFeedInput       input   = new SyndFeedInput();
        SyndFeed            feed    = input.build(new XmlReader(uri.toURL()));
        Iterable<SyndEntry> entries = (List<SyndEntry>) feed.getEntries();

        List<T> items = new ArrayList<>();

        for (SyndEntry entry : entries) {
            items.add(converter.applyWithException(entry));
        }

        return items;
    }

}
