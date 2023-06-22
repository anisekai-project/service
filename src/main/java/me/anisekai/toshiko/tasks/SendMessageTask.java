package me.anisekai.toshiko.tasks;

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

    /**
     * Called when this {@link Task} {@link #run()} method has completed successfully.
     */
    @Override
    public void onFinished() {

    }

    /**
     * Called when this {@link Task} {@link #run()} has thrown an exception.
     *
     * @param e
     *         The {@link Exception} that has been thrown.
     */
    @Override
    public void onException(Exception e) {

    }

    @Override
    public void run() throws Exception {

        this.channel.sendMessage(MessageCreateData.fromEmbeds(this.embeddable.asEmbed().build())).complete();
    }

}
