package me.anisekai.modules.toshiko.utils;

import me.anisekai.api.json.BookshelfArray;
import me.anisekai.api.json.BookshelfJson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public final class EmbeddingUtils {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private EmbeddingUtils() {}

    public static BookshelfJson toJson(MessageEmbed embed) {

        BookshelfJson json = new BookshelfJson();
        json.put("title", embed.getTitle());
        json.put("url", embed.getUrl());
        json.put("description", embed.getDescription());
        json.put("timestamp", Optional.ofNullable(embed.getTimestamp()).map(DTF::format).orElse(null));
        json.put("color", Optional.ofNullable(embed.getColor()).map(Color::getRGB).orElse(null));

        Optional.ofNullable(embed.getThumbnail()).ifPresent(thumbnail -> {
            json.put("thumbnail.url", thumbnail.getUrl());
        });

        Optional.ofNullable(embed.getAuthor()).ifPresent(author -> {
            json.put("author.name", author.getName());
            json.put("author.url", author.getUrl());
            json.put("author.iconUrl", author.getIconUrl());
        });

        Optional.ofNullable(embed.getFooter()).ifPresent(footer -> {
            json.put("footer.text", footer.getText());
            json.put("footer.iconUrl", footer.getIconUrl());
        });

        Optional.ofNullable(embed.getImage()).ifPresent(image -> {
            json.put("image.url", image.getUrl());
        });

        BookshelfArray fields = new BookshelfArray();
        for (MessageEmbed.Field field : embed.getFields()) {
            BookshelfJson fieldJson = new BookshelfJson();
            fieldJson.put("name", field.getName());
            fieldJson.put("value", field.getValue());
            fieldJson.put("inline", field.isInline());
            fields.put(fieldJson);
        }
        json.put("fields", fields);

        return json;
    }

    public static MessageEmbed fromJson(BookshelfJson json) {

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(json.readString("title"));
        builder.setUrl(json.readString("url"));
        builder.setDescription(json.readString("description"));
        builder.setTimestamp(Optional.ofNullable(json.readString("timestamp")).map(DTF::parse).orElse(null));
        builder.setColor(new Color(json.readInt("color")));
        builder.setThumbnail(json.readString("thumbnail.url"));

        builder.setAuthor(
                json.readString("author.name"),
                json.readString("author.url"),
                json.readString("author.iconUrl")
        );

        builder.setFooter(json.readString("footer.text"), json.readString("footer.iconUrl"));
        builder.setImage(json.readString("image.url"));

        json.readAll(
                    "fields",
                    fieldJson -> new MessageEmbed.Field(
                            fieldJson.readString("name"),
                            fieldJson.readString("value"),
                            json.getBoolean("inline"),
                            true
                    )
            )
            .forEach(builder::addField);

        return builder.build();
    }


}
