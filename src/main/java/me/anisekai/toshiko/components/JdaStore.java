package me.anisekai.toshiko.components;

import me.anisekai.toshiko.configurations.ToshikoDiscordConfiguration;
import me.anisekai.toshiko.exceptions.JdaUnavailableException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JdaStore extends ListenerAdapter {

    private final ToshikoDiscordConfiguration configuration;

    private JDA jda;

    public JdaStore(ToshikoDiscordConfiguration configuration) {

        this.configuration = configuration;
    }

    @Override
    public void onGenericEvent(@NotNull GenericEvent event) {

        this.jda = event.getJDA();
    }

    public Optional<JDA> getInstance() {

        return Optional.ofNullable(this.jda);
    }

    public JDA requireInstance() {

        return this.getInstance().orElseThrow(JdaUnavailableException::new);
    }

    public Guild getBotGuild() {

        JDA instance = this.requireInstance();

        return Optional.ofNullable(instance.getGuildById(this.configuration.getServerId()))
                       .orElseThrow(JdaUnavailableException::new);
    }

    public TextChannel getAnnouncementChannel() {

        return Optional.ofNullable(this.getBotGuild().getTextChannelById(this.configuration.getAnnounceChannelId()))
                       .orElseThrow(JdaUnavailableException::new);
    }

    public TextChannel getWatchlistChannel() {

        return Optional.ofNullable(this.getBotGuild().getTextChannelById(this.configuration.getWatchlistChannelId()))
                       .orElseThrow(JdaUnavailableException::new);
    }

    public Role getAnnouncementRole() {

        return Optional.ofNullable(this.getBotGuild().getRoleById(this.configuration.getAnnounceRoleId()))
                       .orElseThrow(JdaUnavailableException::new);
    }
}
