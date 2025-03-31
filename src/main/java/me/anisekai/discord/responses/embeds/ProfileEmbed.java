package me.anisekai.discord.responses.embeds;

import me.anisekai.server.interfaces.IAnime;
import me.anisekai.server.interfaces.IDiscordUser;
import me.anisekai.server.interfaces.IInterest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.Collection;
import java.util.Optional;

public class ProfileEmbed extends EmbedBuilder {

    public void setUser(User user) {

        this.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
    }

    public void setUser(IDiscordUser user) {

        this.addField("Icône de vote", Optional.ofNullable(user.getEmote()).orElseGet(() -> "*Non défini*"), true);
        this.addField("Accès au site", user.hasWebsiteAccess() ? "Oui" : "Non", true);
        this.addField("Actif", user.isActive() ? "Oui" : "Non", true);
    }

    public void setInterests(Collection<? extends IInterest<?, ?>> interests) {

        long positiveInterests = interests.stream().filter(interest -> interest.getLevel() > 0).count();
        long negativeInterests = interests.stream().filter(interest -> interest.getLevel() < 0).count();

        this.addField("Votes", String.format("%d positifs, %d négatifs", positiveInterests, negativeInterests), true);
    }

    public void setAnimes(Collection<? extends IAnime<?>> animes) {

        this.addField("Nombre d'anime(s) ajouté(s)", String.valueOf(animes.size()), true);
    }

}
