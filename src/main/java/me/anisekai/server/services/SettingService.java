package me.anisekai.server.services;

import me.anisekai.server.entities.Setting;
import me.anisekai.server.entities.adapters.SettingEventAdapter;
import me.anisekai.server.events.SettingCreatedEvent;
import me.anisekai.server.persistence.DataService;
import me.anisekai.server.proxy.SettingProxy;
import me.anisekai.server.repositories.SettingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingService extends DataService<Setting, String, SettingEventAdapter, SettingRepository, SettingProxy> {

    public static final String WATCHLIST_CHANNEL    = "discord.channels.watchlist";
    public static final String ANNOUNCEMENT_CHANNEL = "discord.channels.announcements";
    public static final String AUDIT_CHANNEL        = "discord.channels.audit";
    public static final String ANNOUNCEMENT_ROLE    = "discord.roles.audit";
    public static final String SERVER_ID            = "discord.server";
    public static final String DOWNLOAD_ENABLED     = "application.downloads.enabled";
    public static final String DOWNLOAD_SERVER      = "application.downloads.server";
    public static final String DOWNLOAD_SOURCE      = "application.downloads.source";
    public static final String ANIME_AUTO_ANNOUNCE  = "application.announces.anime";

    public SettingService(SettingProxy proxy) {

        super(proxy);
    }


    private Optional<String> getSetting(String id) {

        return this.getProxy()
                   .fetchEntity(id)
                   .map(Setting::getValue);
    }

    public Setting setSetting(String id, String value) {

        return this.getProxy().upsertEntity(
                id,
                SettingCreatedEvent::new,
                setting -> {
                    setting.setId(id);
                    setting.setValue(value);
                }
        );
    }

    // <editor-fold desc="General Getters">

    public Optional<Long> getWatchlistChannel() {

        return this.getSetting(WATCHLIST_CHANNEL).map(Long::parseLong);
    }

    public Optional<Long> getAnnouncementChannel() {

        return this.getSetting(ANNOUNCEMENT_CHANNEL).map(Long::parseLong);
    }

    public Optional<Long> getAuditChannel() {

        return this.getSetting(AUDIT_CHANNEL).map(Long::parseLong);
    }

    public Optional<Long> getAnnouncementRole() {

        return this.getSetting(ANNOUNCEMENT_ROLE).map(Long::parseLong);
    }

    public Optional<Long> getServerId() {

        return this.getSetting(SERVER_ID).map(Long::parseLong);
    }

    public boolean isDownloadEnabled() {

        return this.getSetting(DOWNLOAD_ENABLED)
                   .map(Boolean::parseBoolean)
                   .orElse(false);
    }

    public boolean isAnimeAnnouncementEnabled() {

        return this.getSetting(ANIME_AUTO_ANNOUNCE)
                   .map(Boolean::parseBoolean)
                   .orElse(false);
    }

    public Optional<String> getDownloadServer() {

        return this.getSetting(DOWNLOAD_SERVER);
    }

    public Optional<String> getDownloadSource() {

        return this.getSetting(DOWNLOAD_SOURCE);
    }

    // </editor-fold>
}
