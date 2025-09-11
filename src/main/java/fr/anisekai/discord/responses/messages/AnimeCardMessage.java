package fr.anisekai.discord.responses.messages;

import fr.alexpado.jda.interactions.responses.SlashResponse;
import fr.anisekai.discord.responses.embeds.AnimeCardEmbed;
import fr.anisekai.wireless.remote.interfaces.AnimeEntity;
import fr.anisekai.wireless.remote.interfaces.InterestEntity;
import fr.anisekai.wireless.remote.interfaces.UserEntity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class AnimeCardMessage implements SlashResponse {


    private final AnimeEntity<? extends UserEntity>          anime;
    private final Collection<? extends InterestEntity<?, ?>> interests;
    private final Role                                       role;

    public AnimeCardMessage(AnimeEntity<?> anime, Collection<? extends InterestEntity<?, ?>> interests) {

        this(anime, interests, null);
    }

    public AnimeCardMessage(AnimeEntity<?> anime, Collection<? extends InterestEntity<?, ?>> interests, Role role) {

        this.anime     = anime;
        this.interests = interests;
        this.role      = role;
    }

    /**
     * Retrieve the {@link MessageRequest} {@link Consumer} that should set the response content.
     *
     * @return A {@link MessageRequest} {@link Consumer}
     */
    @Override
    public Consumer<MessageRequest<?>> getHandler() {

        return mr -> {
            AnimeCardEmbed animeCard = new AnimeCardEmbed();
            animeCard.setAnime(this.anime);
            animeCard.setInterests(this.interests);

            if (this.role != null) {
                ActionRow buttons = ActionRow.of(
                        Button.success(this.getButtonUrl(2), "Je suis intéressé(e)"),
                        Button.secondary(this.getButtonUrl(0), "Je suis neutre"),
                        Button.danger(this.getButtonUrl(-2), "Je ne suis pas intéressé(e)")
                );

                mr.setContent(String.format("Hey %s ! Un anime a été ajouté !", this.role.getAsMention()))
                  .setAllowedMentions(Collections.singletonList(Message.MentionType.ROLE))
                  .setComponents(buttons);
            }

            mr.setEmbeds(animeCard.build());
        };
    }

    /**
     * Check if this {@link SlashResponse} is ephemeral (ie: Only shown to the user who interacted).
     *
     * @return True if ephemeral, false otherwise.
     */
    @Override
    public boolean isEphemeral() {

        return this.role == null;
    }

    private String getButtonUrl(int level) {

        return String.format("button://interest?anime=%s&interest=%s", this.anime.getId(), level);
    }

}
