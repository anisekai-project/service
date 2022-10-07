package me.anisekai.toshiko.helpers.embeds;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.entities.ScheduledEvent;
import me.anisekai.toshiko.enums.InterestLevel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ScheduledEventEmbed implements SlashResponse, ButtonResponse {

    private final ScheduledEvent event;

    public ScheduledEventEmbed(ScheduledEvent event) {

        this.event = event;
    }

    @Override
    public boolean shouldEditOriginalMessage() {

        return true;
    }

    @Override
    public Consumer<AbstractMessageBuilder<?, ?>> getHandler() {

        return (amb) -> {
            EmbedBuilder builder = new EmbedBuilder();
            Anime        anime   = this.event.getAnime();

            builder.setTitle(anime.getName(), anime.getLink());
            builder.setDescription(this.event.getDescription());
            builder.setColor(this.event.getState().getColor());

            DateTimeFormatter dtf  = DateTimeFormatter.ofPattern("EEEE d MMMM à HH:mm");
            String            date = this.event.getEventStartAt().format(dtf);

            BiConsumer<Anime, EmbedBuilder> progressAppender = (ani, eb) -> {
                if (anime.getWatched() + 1 == this.event.getLastEpisode()) {
                    eb.addField("Épisode", "Épisode %s".formatted(this.event.getLastEpisode()), false);
                } else {
                    eb.addField("Épisode", "%s à %s".formatted(
                            ani.getWatched() + 1,
                            this.event.getLastEpisode()
                    ), false);
                }
            };

            switch (this.event.getState()) {
                case SCHEDULED -> {
                    builder.appendDescription("\n\n**Scéance plannifiée pour le:**");
                    builder.appendDescription("\n" + date);
                    progressAppender.accept(anime, builder);

                    List<String> interestedUsers = anime.getInterests().stream()
                                                        .filter(interest -> interest.getLevel() == InterestLevel.INTERESTED)
                                                        .map(Interest::getUser)
                                                        .map(DiscordUser::getId)
                                                        .map(User::fromId)
                                                        .map(UserSnowflake::getAsMention)
                                                        .toList();

                    List<String> notInterestedUsers = anime.getInterests().stream()
                                                           .filter(interest -> interest.getLevel() == InterestLevel.NOT_INTERESTED)
                                                           .map(Interest::getUser)
                                                           .map(DiscordUser::getId)
                                                           .map(User::fromId)
                                                           .map(UserSnowflake::getAsMention)
                                                           .toList();

                    builder.addField("Personne(s) intéressée(s)", String.join("\n", interestedUsers), false);
                    builder.addField("Personne(s) non intéressée(s)", String.join("\n", notInterestedUsers), false);

                    amb.setActionRow(
                            Button.danger(this.getButtonUrl("cancel"), "Annuler"),
                            Button.success(this.getButtonUrl("start"), "Démarrer")
                    );
                }
                case OPENED -> {
                    builder.appendDescription("\n\n**Scéance ouverte !**");
                    builder.appendDescription("\n" + date);
                    progressAppender.accept(anime, builder);

                    amb.setActionRow(
                            Button.danger(this.getButtonUrl("cancel"), "Annuler"),
                            Button.success(this.getButtonUrl("finish"), "Terminer")
                    );
                }
                case CANCELLED -> {
                    builder.appendDescription("\n\n**La scéance a été annulée !**");
                    amb.setComponents(Collections.emptyList());
                }
                case FINISHED -> {
                    builder.appendDescription("\n\n**La scéance est terminée !**");
                    amb.setComponents(Collections.emptyList());
                }
            }

            builder.setImage(String.format("https://toshiko.alexpado.fr/%s.png", anime.getId()));
            amb.setEmbeds(builder.build());
        };
    }

    private String getButtonUrl(String action) {

        return String.format("button://schedule/%s?id=%s", action, this.event.getId());
    }

    @Override
    public boolean isEphemeral() {

        return false;
    }
}
