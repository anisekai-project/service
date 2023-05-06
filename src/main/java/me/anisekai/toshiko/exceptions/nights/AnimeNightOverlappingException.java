package me.anisekai.toshiko.exceptions.nights;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import me.anisekai.toshiko.entities.Anime;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class AnimeNightOverlappingException extends RuntimeException implements DiscordEmbeddable {

    private final Anime          anime;
    private final OffsetDateTime starting;
    private final OffsetDateTime ending;

    public AnimeNightOverlappingException(Anime anime, OffsetDateTime starting, OffsetDateTime ending) {

        this.anime    = anime;
        this.starting = starting;
        this.ending   = ending;
    }

    @Override
    public EmbedBuilder asEmbed() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return new EmbedBuilder()
                .setTitle("Impossible de planifier la séance !")
                .setDescription(String.format(
                        "Impossible de planifier la séance pour l'anime **%s** car cela entrerait en conflit avec d'autres séances.",
                        this.anime.getName()
                ))
                .addField("Heure de début:", this.ending.toZonedDateTime().format(dtf), false)
                .addField("Heure de fin:", this.starting.toZonedDateTime().format(dtf), false);
    }

    @Override
    public boolean showToEveryone() {

        return true;
    }
}
