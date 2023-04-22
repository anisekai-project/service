package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.Texts;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.helpers.responses.SimpleResponse;
import me.anisekai.toshiko.services.ToshikoService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class UserInteractions {

    private final ToshikoService toshikoService;

    public UserInteractions(ToshikoService toshikoService) {

        this.toshikoService = toshikoService;
    }

    // <editor-fold desc="@ user/icon/set">
    @Interact(
            name = "user/icon/set",
            description = Texts.USER_ICON__DESCRIPTION,
            options = {
                    @Option(
                            name = "icon",
                            description = Texts.USER_ICON__OPTION_ICON,
                            type = OptionType.STRING,
                            required = true
                    )
            }
    )
    public SlashResponse changeUserIcon(DiscordUser discordUser, User user, @Param("icon") String icon) {

        if (!this.toshikoService.setUserEmoji(user, icon)) {
            return new SimpleResponse("Ton icône de vote reste inchangée.", false, true);
        }

        if (!discordUser.isActive()) {
            this.toshikoService.queueUpdateAll(false);
        }

        return new SimpleResponse("Ton icône de vote a été mise à jour.", false, false);
    }
    // </editor-fold>


    // <editor-fold desc="@ profile">
    @Interact(
            name = "profile",
            description = Texts.PROFILE_DESCRIPTION,
            options = {
                    @Option(
                            name = "user",
                            description = Texts.PROFILE__OPTION_USER,
                            type = OptionType.USER
                    ),
                    @Option(
                            name = "icon",
                            description = Texts.PROFILE__OPTION_ICON,
                            type = OptionType.STRING
                    ),
                    @Option(
                            name = "active",
                            description = Texts.PROFILE__OPTION_ACTIVE,
                            type = OptionType.BOOLEAN
                    ),
                    @Option(
                            name = "admin",
                            description = Texts.PROFILE__OPTION_ADMIN,
                            type = OptionType.BOOLEAN
                    )
            }
    )
    public SlashResponse updateUserProfile(
            DiscordUser sender,
            @Param("user") User user,
            @Param("icon") String icon,
            @Param("active") Boolean active,
            @Param("admin") Boolean admin
    ) {

        if (((user != null) || admin != null || active != null) && !sender.isAdmin()) {
            if (!sender.isAdmin()) {
                return new SimpleResponse("Seul un administrateur peut modifier ces informations.", false, false);
            }
        }

        if (icon == null && active == null && admin == null) {
            return new SimpleResponse("Aucune information actualisée.", false, false);
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();


        DiscordUser target = sender;

        if (user != null) {
            target = this.toshikoService.findUser(user);
        }

        UserSnowflake targetedUser = UserSnowflake.fromId(sender.getId());
        embedBuilder.setDescription("Modification(s) effectuée(s) sur l'utilisateur " + targetedUser.getAsMention());

        if (admin != null) {
            target.setAdmin(admin);
            embedBuilder.addField("Statut administrateur", admin ? "Administrateur" : "Membre", false);
        }

        if (active != null) {
            target.setActive(active);
            embedBuilder.addField("Statut d'activité", active ? "Actif" : "Non actif", false);
        }

        if (icon != null) {
            if (!this.toshikoService.setUserEmoji(target, icon)) {
                embedBuilder.addField("Icône de vote", target.getEmote() + " *(Inchangé)*", false);
            } else {
                embedBuilder.addField("Icône de vote", target.getEmote() != null ? target.getEmote() : "*Aucune*", false);
            }
        }

        return new SimpleResponse(embedBuilder, false, false);
    }
    // </editor-fold>
}
