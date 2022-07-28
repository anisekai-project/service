package me.anisekai.toshiko.helpers.embeds;

import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.helpers.DateHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Collections;
import java.util.List;

public class AnimeSheetMessage {

    private final Anime          anime;
    private final List<Interest> interests;

    public AnimeSheetMessage(Anime anime, List<Interest> interests) {

        this.anime     = anime;
        this.interests = interests;
    }

    public Message getMessage(boolean showButtons, String mainContent) {

        List<String> interestedUsers = this.interests.stream()
                                                     .filter(interest -> interest.getLevel() == InterestLevel.INTERESTED)
                                                     .map(Interest::getUser)
                                                     .map(DiscordUser::getId)
                                                     .map(User::fromId)
                                                     .map(UserSnowflake::getAsMention)
                                                     .toList();

        List<String> notInterestedUsers = this.interests.stream()
                                                        .filter(interest -> interest.getLevel() == InterestLevel.NOT_INTERESTED)
                                                        .map(Interest::getUser)
                                                        .map(DiscordUser::getId)
                                                        .map(User::fromId)
                                                        .map(UserSnowflake::getAsMention)
                                                        .toList();

        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder   embedBuilder   = new EmbedBuilder();

        messageBuilder.setAllowedMentions(Collections.singletonList(Message.MentionType.ROLE));
        //AnimeProvider provider = AnimeProvider.of(this.anime.getLink());

        embedBuilder.setTitle(this.anime.getName());
        embedBuilder.setTitle(this.anime.getName(), this.anime.getLink());
        //embedBuilder.setDescription(provider.getSynopsis());
        embedBuilder.setDescription("*Récupération des données impossible*");
        embedBuilder.addField("Tag(s)", "*Récupération des données impossible*", false);
        //embedBuilder.setThumbnail(provider.getImage());

        embedBuilder.addField("Nombre d'épisode", "*Récupération des données impossible*", false);
        embedBuilder.addField("Note", "*Récupération des données impossible*", false);

        /*provider.getEpisodeCount()
                .ifPresent(epCount -> embedBuilder.addField("Nombre d'épisode", epCount.toString(), false));
        provider.getRating().ifPresent(rating -> embedBuilder.addField("Note", rating.toString(), false));*/

        embedBuilder.addField("Proposé par", User.fromId(this.anime.getAddedBy().getId()).getAsMention(), false);
        embedBuilder.addField("Statut", this.anime.getStatus().getDisplay(), false);
        embedBuilder.addField("Ajouté le", DateHelper.format(this.anime.getAddedAt()), false);

        embedBuilder.addField("Personne(s) intéressée(s)", String.join("\n", interestedUsers), false);
        embedBuilder.addField("Personne(s) non intéressée(s)", String.join("\n", notInterestedUsers), false);

        if (mainContent != null) {
            messageBuilder.setContent(mainContent);
        }

        if (showButtons) {
            messageBuilder.setActionRows(ActionRow.of(
                    Button.success(this.getButtonUrl(InterestLevel.INTERESTED), Texts.ANIME_INTEREST__OPTION_LEVEL__CHOICE_INTERESTED),
                    Button.secondary(this.getButtonUrl(InterestLevel.NEUTRAL), Texts.ANIME_INTEREST__OPTION_LEVEL__CHOICE_NEUTRAL),
                    Button.danger(this.getButtonUrl(InterestLevel.NOT_INTERESTED), Texts.ANIME_INTEREST__OPTION_LEVEL__CHOICE_NOT_INTERESTED)
            ));
        }

        messageBuilder.setEmbeds(embedBuilder.build());
        return messageBuilder.build();
    }

    private String getButtonUrl(InterestLevel level) {

        return String.format("button://anime/interest?anime=%s&interest=%s", this.anime.getId(), level.name());
    }

}
