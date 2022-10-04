package me.anisekai.toshiko.helpers.embeds;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.helpers.DateHelper;
import me.anisekai.toshiko.interfaces.AnimeProvider;
import me.anisekai.toshiko.providers.OfflineProvider;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class AnimeSheetMessage implements SlashResponse, ButtonResponse {

    private final Anime          anime;
    private final List<Interest> interests;
    private       boolean        showButtons;
    private       String         content;

    public AnimeSheetMessage(Anime anime, List<Interest> interests) {

        this.anime     = anime;
        this.interests = interests;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public void setShowButtons(boolean showButtons) {

        this.showButtons = showButtons;
    }

    @Override
    public boolean shouldEditOriginalMessage() {

        return true;
    }

    private AnimeProvider getProvider() {

        try {
            return AnimeProvider.of(this.anime.getLink());
        } catch (Exception e) {
            return new OfflineProvider(this.anime);
        }
    }

    @Override
    public Consumer<AbstractMessageBuilder<?, ?>> getHandler() {

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

        AnimeProvider provider = this.getProvider();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(provider.getName(), provider.getUrl());
        builder.setDescription(provider.getSynopsis());

        if (!provider.getTags().isEmpty()) {
            builder.addField("Tag(s)", String.join(", ", provider.getTags()), false);
        }

        if (provider.getImage() != null) {
            builder.setThumbnail(provider.getImage());
        }

        provider.getEpisodeCount()
                .ifPresent(epCount -> builder.addField("Nombre d'épisode", epCount.toString(), false));
        provider.getRating().ifPresent(rating -> builder.addField("Note", rating.toString(), false));

        builder.addField("Proposé par", User.fromId(this.anime.getAddedBy().getId()).getAsMention(), false);
        builder.addField("Statut", this.anime.getStatus().getDisplay(), false);
        builder.addField("Ajouté le", DateHelper.format(this.anime.getAddedAt()), false);

        builder.addField("Personne(s) intéressée(s)", String.join("\n", interestedUsers), false);
        builder.addField("Personne(s) non intéressée(s)", String.join("\n", notInterestedUsers), false);

        ActionRow buttons = ActionRow.of(
                Button.success(this.getButtonUrl(InterestLevel.INTERESTED), Texts.ANIME_INTEREST__OPTION_LEVEL__CHOICE_INTERESTED),
                Button.secondary(this.getButtonUrl(InterestLevel.NEUTRAL), Texts.ANIME_INTEREST__OPTION_LEVEL__CHOICE_NEUTRAL),
                Button.danger(this.getButtonUrl(InterestLevel.NOT_INTERESTED), Texts.ANIME_INTEREST__OPTION_LEVEL__CHOICE_NOT_INTERESTED)
        );

        return (amb) -> {
            amb.setAllowedMentions(Collections.singletonList(Message.MentionType.ROLE))
               .setContent(this.content)
               .setEmbeds(builder.build());

            if (this.showButtons) {
                amb.setComponents(buttons);
            }
        };
    }

    @Override
    public boolean isEphemeral() {

        return false;
    }

    private String getButtonUrl(InterestLevel level) {

        return String.format("button://anime/interest?anime=%s&interest=%s", this.anime.getId(), level.name());
    }
}
