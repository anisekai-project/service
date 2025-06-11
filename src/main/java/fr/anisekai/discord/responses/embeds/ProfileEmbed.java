package fr.anisekai.discord.responses.embeds;

import fr.anisekai.wireless.remote.interfaces.AnimeEntity;
import fr.anisekai.wireless.remote.interfaces.InterestEntity;
import fr.anisekai.wireless.remote.interfaces.UserEntity;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.Collection;
import java.util.Optional;

public class ProfileEmbed extends EmbedBuilder {

    public void setUser(User user) {

        this.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
    }

    public void setUser(UserEntity user) {

        this.addField("Icône de vote", Optional.ofNullable(user.getEmote()).orElse("*Non défini*"), true);
        this.addField("Accès au site", user.isGuest() ? "Non" : "Oui", true);
        this.addField("Actif", user.isActive() ? "Oui" : "Non", true);
    }

    public void setInterests(Collection<? extends InterestEntity<?, ?>> interests) {

        long positiveInterests = interests.stream().filter(interest -> interest.getLevel() > 0).count();
        long negativeInterests = interests.stream().filter(interest -> interest.getLevel() < 0).count();

        this.addField("Votes", String.format("%d positifs, %d négatifs", positiveInterests, negativeInterests), true);
    }

    public void setAnimes(Collection<? extends AnimeEntity<?>> animes) {

        this.addField("Nombre d'anime(s) ajouté(s)", String.valueOf(animes.size()), true);
    }

}
