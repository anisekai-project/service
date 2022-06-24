package me.anisekai.toshiko.helpers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class JsoupHelper {

    private final Document document;

    public JsoupHelper(Document document) {

        this.document = document;
    }

    public Optional<String> readClassContent(String className) {

        Elements elements = this.document.select("#content ." + className);
        return elements.isEmpty() ? Optional.empty() : Optional.of(elements.text());
    }

    public Optional<String> readPropertyContent(String property) {

        Elements elements = this.document.select(String.format("#content [itemprop='%s']", property));
        return elements.size() == 1 ? Optional.of(elements.get(0).text()) : Optional.empty();
    }

    public Optional<String> readMetaContent(String property) {

        Elements elements = this.document.select(String.format("meta[property='%s']", property));
        return elements.size() == 1 ? Optional.of(elements.attr("content")) : Optional.empty();
    }

    public Set<String> readPropertiesContent(String property) {

        Elements    elements = this.document.select(String.format("#content [itemprop='%s']", property));
        Set<String> items    = new HashSet<>();
        for (Element element : elements) {
            items.add(element.text());
        }
        return items;
    }

}
