package me.anisekai.toshiko;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import me.anisekai.toshiko.helpers.JDAStore;
import me.anisekai.toshiko.services.InteractionWrapper;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ToshikoBot extends ListenerAdapter {

    private static final Logger             LOGGER = LoggerFactory.getLogger(ToshikoBot.class);
    private final        InteractionWrapper wrapper;
    private final        JDAStore           store;

    @Value("${discord.bot.token}")
    private String token;

    @Value("${toshiko.anime.server}")
    private long toshikoAnimeServer;

    public ToshikoBot(InteractionWrapper wrapper, JDAStore store) {

        this.wrapper = wrapper;
        this.store   = store;
    }

    public final void login() {

        try {
            JDABuilder builder = JDABuilder.create(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
            builder.addEventListeners(this, this.store);
            builder.setToken(this.token);
            builder.build();
        } catch (Exception e) {
            LOGGER.warn("Unable to connect to Discord. The token provided is probably invalid.", e);

            Sentry.withScope(scope -> {
                scope.setLevel(SentryLevel.FATAL);
                Sentry.captureException(e);
            });
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        // Refresh roles in the database.
        User self = event.getJDA().getSelfUser();
        LOGGER.info("== == == == == == == Toshiko Bot == == == == == == ==");
        LOGGER.info("Logged in as '{} ({})'", self.getName(), self.getId());

        Guild guild = event.getJDA().getGuildById(this.toshikoAnimeServer);

        if (guild == null) {
            LOGGER.error("Unable to find the discord server [{}]", this.toshikoAnimeServer);
            return;
        }

        this.wrapper.hook(guild);
        LOGGER.info("Successfully hooked to server '{} ({})'", guild.getName(), guild.getId());
        LOGGER.info("== == == == == == == == == == == == == == == == == ==");
    }


}
