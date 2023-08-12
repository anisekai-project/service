package me.anisekai.toshiko.modules.discord.messages.embeds;

import fr.alexpado.jda.interactions.responses.ButtonResponse;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.modules.discord.Texts;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.Interest;
import me.anisekai.toshiko.enums.InterestLevel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AnimeEmbed implements SlashResponse, ButtonResponse {

    private final Anime         anime;
    private final Set<Interest> interests;
    private final double        score;
    private       boolean       showButtons;
    private       String        content;

    public AnimeEmbed(Anime anime, double score) {

        this.score     = score;
        this.anime     = anime;
        this.interests = anime.getInterests();
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

    @Override
    public Consumer<MessageRequest<?>> getHandler() {

        List<String> interestedUsers = this.interests.stream()
                                                     .filter(interest -> interest.getLevel() == InterestLevel.INTERESTED)
                                                     .map(Interest::getUser)
                                                     .map(DiscordUser::getId)
                                                     .sorted()
                                                     .map(User::fromId)
                                                     .map(UserSnowflake::getAsMention)
                                                     .toList();

        List<String> notInterestedUsers = this.interests.stream()
                                                        .filter(interest -> interest.getLevel() == InterestLevel.NOT_INTERESTED)
                                                        .map(Interest::getUser)
                                                        .map(DiscordUser::getId)
                                                        .sorted()
                                                        .map(User::fromId)
                                                        .map(UserSnowflake::getAsMention)
                                                        .toList();


        EmbedBuilder builder = new EmbedBuilder();
        if (this.score > 0) {
            builder.setTitle(String.format("%s (%.2f)", this.anime.getName(), this.score), this.anime.getLink());
        } else {
            builder.setTitle(this.anime.getName(), this.anime.getLink());
        }

        if (this.anime.getSynopsis() != null) {
            builder.setDescription(this.anime.getSynopsis());
        } else {
            builder.setDescription("*Impossible de récupérer le synopsis.*");
        }

        if (this.anime.getGenres() != null) {
            builder.addField("Genre(s)", this.anime.getGenres(), false);
        }

        if (this.anime.getThemes() != null) {
            builder.addField("Thème(s)", this.anime.getThemes(), false);
        }

        if (this.anime.getImage() != null) {
            builder.setThumbnail(this.anime.getImage());
        }
        builder.addField("Nombre d'épisode", String.valueOf(this.anime.getTotal()), false);

        builder.addField("Proposé par", User.fromId(this.anime.getAddedBy().getId()).getAsMention(), false);
        builder.addField("Statut", this.anime.getStatus().getDisplay(), false);

        builder.addField(
                "Ajouté le",
                this.anime.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm:ss")),
                false
        );

        builder.addField("Personne(s) intéressée(s)", String.join("\n", interestedUsers), false);
        builder.addField("Personne(s) non intéressée(s)", String.join("\n", notInterestedUsers), false);

        ActionRow buttons = ActionRow.of(
                Button.success(
                        this.getButtonUrl(InterestLevel.INTERESTED),
                        Texts.ANIME_INTEREST__OPTION_LEVEL__CHOICE_INTERESTED
                ),
                Button.secondary(
                        this.getButtonUrl(InterestLevel.NEUTRAL),
                        Texts.ANIME_INTEREST__OPTION_LEVEL__CHOICE_NEUTRAL
                ),
                Button.danger(
                        this.getButtonUrl(InterestLevel.NOT_INTERESTED),
                        Texts.ANIME_INTEREST__OPTION_LEVEL__CHOICE_NOT_INTERESTED
                )
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

        return String.format("button://interest/set?anime=%s&interest=%s", this.anime.getId(), level.name());
    }

}
