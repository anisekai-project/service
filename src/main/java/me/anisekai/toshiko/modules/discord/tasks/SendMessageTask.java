package me.anisekai.toshiko.modules.discord.tasks;

import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import me.anisekai.toshiko.data.Task;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class SendMessageTask implements Task {

    private final TextChannel       channel;
    private final DiscordEmbeddable embeddable;

    public SendMessageTask(TextChannel channel, DiscordEmbeddable embeddable) {

        this.channel    = channel;
        this.embeddable = embeddable;
    }

    /**
     * Retrieve this {@link Task} name.
     *
     * @return The name
     */
    @Override
    public String getName() {

        return String.format("SEND:%s:MESSAGE", this.embeddable.hashCode());
    }

    @Override
    public void run() {

        this.channel.sendMessage(MessageCreateData.fromEmbeds(this.embeddable.asEmbed().build())).complete();
    }

}
