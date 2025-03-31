package me.anisekai.discord.responses.embeds;

import me.anisekai.server.interfaces.IAnime;
import me.anisekai.server.interfaces.IDiscordUser;
import me.anisekai.server.interfaces.IInterest;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.stream.Collectors;

public class AnimeCardEmbed extends EmbedBuilder {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy 'à' HH:mm");

    public void setAnime(IAnime<? extends IDiscordUser> anime) {

        String episodeText;

        if (anime.getTotal() == 0) {
            episodeText = "*Inconnu*";
        } else if (anime.getTotal() < 0) {
            episodeText = String.format("%s *(Estimation)*", anime.getTotal() * -12);
        } else {
            episodeText = String.valueOf(anime.getTotal());
        }

        this.setTitle(anime.getTitle(), anime.getNautiljonUrl());
        this.setThumbnail(anime.getThumbnail());
        this.setDescription(anime.getSynopsis());

        this.addField("Tags", anime.getTags(), true);
        this.addField("Nombre d'épisodes", episodeText, true);
        this.addBlankField(true);

        this.addField("Statut", anime.getWatchlist().getDisplay(), true);
        this.addField("Ajouté par", String.format("<@%s>", anime.getAddedBy().getId()), true);
        this.addField("Ajouté le", anime.getCreatedAt().format(DATE_FORMATTER), true);
    }

    public void setInterests(Collection<? extends IInterest<? extends IDiscordUser, ?>> interests) {

        String positiveIcons = interests.stream()
                                        .filter(interest -> interest.getLevel() > 0)
                                        .map(IInterest::getUser)
                                        .map(IDiscordUser::getEmote)
                                        .collect(Collectors.joining(" "));

        String negativeIcons = interests.stream()
                                        .filter(interest -> interest.getLevel() < 0)
                                        .map(IInterest::getUser)
                                        .map(IDiscordUser::getEmote)
                                        .collect(Collectors.joining(" "));

        this.addField("Personne(s) intéressée(s)", positiveIcons, true);
        this.addField("Personne(s) non intéressée(s)", negativeIcons, true);

    }

}
