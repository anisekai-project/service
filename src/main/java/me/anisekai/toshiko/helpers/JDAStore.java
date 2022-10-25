package me.anisekai.toshiko.helpers;

import me.anisekai.toshiko.exceptions.JdaUnavailableException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JDAStore extends ListenerAdapter {

    private JDA jda;

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
}
