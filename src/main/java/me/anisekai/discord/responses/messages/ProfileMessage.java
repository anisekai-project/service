package me.anisekai.discord.responses.messages;

import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.discord.responses.embeds.ProfileEmbed;
import me.anisekai.server.interfaces.IAnime;
import me.anisekai.server.interfaces.IDiscordUser;
import me.anisekai.server.interfaces.IInterest;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.util.List;
import java.util.function.Consumer;

public class ProfileMessage implements SlashResponse {

    private final User                            user;
    private final IDiscordUser                    discordUser;
    private final List<? extends IAnime<?>>       animes;
    private final List<? extends IInterest<?, ?>> interests;


    public ProfileMessage(User user, IDiscordUser discordUser, List<? extends IAnime<?>> animes, List<? extends IInterest<?, ?>> interests) {

        this.user        = user;
        this.discordUser = discordUser;
        this.animes      = animes;
        this.interests   = interests;
    }

    /**
     * Retrieve the {@link MessageRequest} {@link Consumer} that should set the response content.
     *
     * @return A {@link MessageRequest} {@link Consumer}
     */
    @Override
    public Consumer<MessageRequest<?>> getHandler() {

        return mr -> {
            ProfileEmbed profile = new ProfileEmbed();
            profile.setUser(this.user);
            profile.setUser(this.discordUser);
            profile.setAnimes(this.animes);
            profile.setInterests(this.interests);

            mr.setEmbeds(profile.build());
        };
    }

    /**
     * Check if this {@link SlashResponse} is ephemeral (ie: Only shown to the user who interacted).
     *
     * @return True if ephemeral, false otherwise.
     */
    @Override
    public boolean isEphemeral() {

        return false;
    }

}
