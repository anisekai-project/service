package me.anisekai.toshiko.modules.discord.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.modules.discord.Texts;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.modules.discord.annotations.InteractionBean;
import me.anisekai.toshiko.modules.discord.messages.responses.SimpleResponse;
import me.anisekai.toshiko.services.UserService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class UserInteractions {

    private final UserService service;

    public UserInteractions(UserService service) {

        this.service = service;
    }

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
                    ),
                    @Option(
                            name = "web",
                            description = Texts.PROFILE__OPTION_WEB,
                            type = OptionType.BOOLEAN
                    )
            }
    )
    public SlashResponse updateUserProfile(
            DiscordUser sender,
            @Param("user") User user,
            @Param("icon") String icon,
            @Param("active") Boolean active,
            @Param("admin") Boolean admin,
            @Param("web") Boolean web
    ) {

        if ((user != null || admin != null || active != null || web != null) && !sender.isAdmin()) {
            if (!sender.isAdmin()) {
                return new SimpleResponse("Seul un administrateur peut modifier ces informations.", false, false);
            }
        }

        if (icon == null && active == null && admin == null && web == null) {
            return new SimpleResponse("Aucune information actualisée.", false, false);
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();


        DiscordUser target = sender;

        if (user != null) {
            target = this.service.get(user);
        }

        UserSnowflake targetedUser = UserSnowflake.fromId(target.getId());
        embedBuilder.setDescription("Modification(s) effectuée(s) sur l'utilisateur " + targetedUser.getAsMention());

        if (admin != null) {
            target.setAdmin(admin);
            embedBuilder.addField("Statut administrateur", admin ? "Administrateur" : "Membre", false);
        }

        if (active != null) {
            target.setActive(active);
            embedBuilder.addField("Statut d'activité", active ? "Actif" : "Non actif", false);
        }

        if (web != null) {
            target.setWebAccess(web);
            embedBuilder.addField("Accès Web", web ? "Autorisé" : "Non autorisé", false);
        }

        if (icon != null) {
            if (!this.service.setUserEmote(target, icon)) {
                embedBuilder.addField("Icône de vote", target.getEmote() + " *(Inchangé)*", false);
            } else {
                embedBuilder.addField(
                        "Icône de vote",
                        target.getEmote() != null ? target.getEmote() : "*Aucune*",
                        false
                );
            }
        }

        this.service.save(target);
        return new SimpleResponse(embedBuilder, false, false);
    }
    // </editor-fold>
}
