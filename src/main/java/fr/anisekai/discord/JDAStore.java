package fr.anisekai.discord;


import fr.anisekai.server.services.SettingService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JDAStore extends ListenerAdapter {

    private final SettingService settings;
    private       JDA            jda = null;

    public JDAStore(SettingService settings) {

        this.settings = settings;
    }

    @Override
    public void onGenericEvent(GenericEvent event) {

        this.jda = event.getJDA();
    }

    public Optional<JDA> getJDA() {

        return Optional.ofNullable(this.jda);
    }

    public Optional<Guild> getGuild() {

        return this.settings.getServerId().flatMap(id -> this.getJDA().map(jda -> jda.getGuildById(id)));
    }

    public Guild requireGuild() {

        return this.getGuild().orElseThrow(() -> new IllegalStateException("Guild is not available"));
    }

    public Optional<TextChannel> getChannel(long id) {

        return this.getGuild().map(guild -> guild.getTextChannelById(id));
    }

    public TextChannel requireChannel(long id) {

        return this.getChannel(id)
                   .orElseThrow(() -> new IllegalStateException(String.format("Channel %s is not available", id)));
    }

    public Optional<Role> getRole(long id) {

        return this.getGuild().map(guild -> guild.getRoleById(id));
    }

    public Role requireRole(long id) {

        return this.getRole(id)
                   .orElseThrow(() -> new IllegalStateException(String.format("Role %s is not available", id)));
    }

    public Optional<Role> getAnnouncementRole() {

        return this.settings.getAnnouncementRole().flatMap(this::getRole);
    }

    public Role requireAnnouncementRole() {

        return this.getAnnouncementRole()
                   .orElseThrow(() -> new IllegalStateException("Announcement role is not available"));
    }

    public Optional<TextChannel> getWatchlistChannel() {

        return this.settings.getWatchlistChannel().flatMap(this::getChannel);
    }

    public TextChannel requireWatchlistChannel() {

        return this.getWatchlistChannel()
                   .orElseThrow(() -> new IllegalStateException("Watchlist channel is not available"));
    }

    public Optional<TextChannel> getAnnouncementChannel() {

        return this.settings.getAnnouncementChannel().flatMap(this::getChannel);
    }

    public TextChannel requireAnnouncementChannel() {

        return this.getAnnouncementChannel()
                   .orElseThrow(() -> new IllegalStateException("Announcement channel is not available"));
    }

    public Optional<TextChannel> getAuditChannel() {

        return this.settings.getAuditChannel().flatMap(this::getChannel);
    }

    public TextChannel requireAuditChannel() {

        return this.getAuditChannel().orElseThrow(() -> new IllegalStateException("Audit channel is not available"));
    }

}
