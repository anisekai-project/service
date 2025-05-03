package me.anisekai.discord.interactions.administation;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.enums.SlashTarget;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import fr.anisekai.wireless.remote.interfaces.UserEntity;
import me.anisekai.discord.annotations.InteractionBean;
import me.anisekai.discord.exceptions.RequireAdministratorException;
import me.anisekai.discord.responses.DiscordResponse;
import me.anisekai.server.services.SettingService;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class SettingInteractions {

    private final SettingService service;

    public SettingInteractions(SettingService service) {

        this.service = service;
    }

    private static void requireAdministrator(UserEntity user) {

        if (!user.isAdministrator()) {
            throw new RequireAdministratorException();
        }
    }

    // <editor-fold desc="@ setting/watchlist-channel ─ Set the channel that will be used for watchlists. [channel: Channel]">
    @Interact(
            name = "setting/watchlist-channel",
            description = "\uD83D\uDD12 — Défini le salon qui sera utilisé pour les listes de visionnage.",
            target = SlashTarget.GUILD,
            options = {
                    @Option(
                            name = "channel",
                            description = "Le salon a définir pour cette option",
                            type = OptionType.CHANNEL,
                            required = true
                    )
            }
    )
    public SlashResponse settingWatchlistChannel(UserEntity user, @Param("channel") Channel channel) {

        requireAdministrator(user);

        if (channel.getType() == ChannelType.TEXT) {
            this.service.setSetting(SettingService.WATCHLIST_CHANNEL, channel.getId());
            return DiscordResponse.info("Les listes d'anime seront envoyées dans %s.", channel.getAsMention());
        }

        return DiscordResponse.error("Merci de choisir un channel textuel pour envoyer les listes.");
    }
    // </editor-fold>

    // <editor-fold desc="@ setting/announcement-role ─ Set the role that will be mentioned for anime announcements. [channel: Channel]">
    @Interact(
            name = "setting/announcement-role",
            description = "\uD83D\uDD12 — Défini le role qui sera utilisé pour les annonces.",
            target = SlashTarget.GUILD,
            options = {
                    @Option(
                            name = "role",
                            description = "Le role a définir pour cette option",
                            type = OptionType.ROLE,
                            required = true
                    )
            }
    )
    public SlashResponse settingAnnouncementRole(UserEntity user, @Param("role") Role role) {

        requireAdministrator(user);

        this.service.setSetting(SettingService.ANNOUNCEMENT_ROLE, role.getId());
        return DiscordResponse.info("Le role %s sera utilisé pour les annonces d'anime.", role.getAsMention());
    }
    // </editor-fold>

    // <editor-fold desc="@ setting/announcement-channel ─ Set the channel that will be used for anime announcements. [channel: Channel]">
    @Interact(
            name = "setting/announcement-channel",
            description = "\uD83D\uDD12 — Défini le salon qui sera utilisé pour les annonces.",
            target = SlashTarget.GUILD,
            options = {
                    @Option(
                            name = "channel",
                            description = "Le salon a définir pour cette option",
                            type = OptionType.CHANNEL,
                            required = true
                    )
            }
    )
    public SlashResponse settingAnnouncementChannel(UserEntity user, @Param("channel") Channel channel) {

        requireAdministrator(user);

        if (channel.getType() == ChannelType.TEXT) {
            this.service.setSetting(SettingService.ANNOUNCEMENT_CHANNEL, channel.getId());
            return DiscordResponse.info("Les annonces d'anime seront envoyées dans %s.", channel.getAsMention());
        }

        return DiscordResponse.error("Merci de choisir un channel textuel pour envoyer les annonces.");
    }
    // </editor-fold>

    // <editor-fold desc="@ setting/audit-channel ─ Set the channel that will be used for log messages. [channel: ?Channel]">
    @Interact(
            name = "setting/audit-channel",
            description = "\uD83D\uDD12 — Défini le salon qui sera utilisé pour les messages d'administration.",
            target = SlashTarget.GUILD,
            options = {
                    @Option(
                            name = "channel",
                            description = "Le salon a définir pour cette option",
                            type = OptionType.CHANNEL,
                            required = true
                    )
            }
    )
    public SlashResponse settingAuditChannel(UserEntity user, @Param("channel") Channel channel) {

        requireAdministrator(user);

        if (channel.getType() == ChannelType.TEXT) {
            this.service.setSetting(SettingService.AUDIT_CHANNEL, channel.getId());
            return DiscordResponse.info(
                    "Les messages d'administration seront envoyés dans %s.",
                    channel.getAsMention()
            );
        }

        return DiscordResponse.error("Merci de choisir un channel textuel pour envoyer les messages d'administration.");
    }
    // </editor-fold>

    // <editor-fold desc="@ setting/enable-auto-download ─ Enable or disable automated downloads. [value: boolean]">
    @Interact(
            name = "setting/enable-auto-download",
            description = "\uD83D\uDD12 — Active ou désactive le téléchargement automatique des épisodes.",
            target = SlashTarget.ALL,
            options = {
                    @Option(
                            name = "value",
                            description = "Valeur de l'option",
                            type = OptionType.BOOLEAN,
                            required = true
                    )
            }
    )
    public SlashResponse settingAutoDownload(UserEntity user, @Param("value") boolean value) {

        requireAdministrator(user);

        this.service.setSetting(SettingService.DOWNLOAD_ENABLED, Boolean.toString(value));
        return DiscordResponse.info("Les téléchargements automatiques ont été %s.", value ? "activés" : "désactivés");
    }
    // </editor-fold>

    // <editor-fold desc="@ setting/download-server ─ Set the URL pointing to the download server. [value: string]">
    @Interact(
            name = "setting/download-server",
            description = "\uD83D\uDD12 — Défini le serveur de téléchargement (Transmission).",
            target = SlashTarget.ALL,
            options = {
                    @Option(
                            name = "value",
                            description = "URL du serveur",
                            type = OptionType.STRING,
                            required = true
                    )
            }
    )
    public SlashResponse settingDownloadServer(UserEntity user, @Param("value") String value) {

        requireAdministrator(user);

        this.service.setSetting(SettingService.DOWNLOAD_SERVER, value);
        return DiscordResponse.info("Les téléchargements automatiques seront effectué via `%s`.", value);
    }
    // </editor-fold>

    // <editor-fold desc="@ setting/download-source ─ Set the URL pointing to the download source. [value: string]">
    @Interact(
            name = "setting/download-source",
            description = "\uD83D\uDD12 — Défini le lien pour la source de téléchargement (Torrent).",
            target = SlashTarget.ALL,
            options = {
                    @Option(
                            name = "value",
                            description = "URL RSS Torrent",
                            type = OptionType.STRING,
                            required = true
                    )
            }
    )
    public SlashResponse settingDownloadSource(UserEntity user, @Param("value") String value) {

        requireAdministrator(user);

        this.service.setSetting(SettingService.DOWNLOAD_SOURCE, value);
        return DiscordResponse.info("La source de téléchargement a été défini sur `%s`.", value);
    }
    // </editor-fold>

}
