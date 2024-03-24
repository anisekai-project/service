package me.anisekai.modules.toshiko.utils;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionErrorHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import io.sentry.Sentry;
import io.sentry.protocol.SentryId;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToshikoErrorHandler implements InteractionErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToshikoErrorHandler.class);

    @NotNull
    private static <T extends Interaction> EmbedBuilder buildEmbed(DispatchEvent<T> event, SentryId sentryId) {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Une erreur est survenue.");
        builder.setDescription("""
                                       Une erreur est survenue lors du traitement de cette action. Merci de réessayer.
                                                                          
                                       Si l'erreur persiste, merci de dire à <@149279150648066048> que c'est un mauvais développeur.
                                       """);

        builder.setFooter(String.format("I-Path: %s • ID: %s", event.getPath().toString(), sentryId));
        return builder;
    }

    /**
     * Called when an exception occurs during the execution of an {@link Interaction}.
     *
     * @param event
     *         The {@link DispatchEvent} that was being executed when the exception was thrown.
     * @param exception
     *         The thrown {@link Exception}.
     */
    @Override
    public <T extends Interaction> void handleException(DispatchEvent<T> event, Exception exception) {

        SentryId sentryId = null;
        //noinspection ChainOfInstanceofChecks
        if (!(exception instanceof DiscordEmbeddable)) {
            sentryId = Sentry.captureException(exception); // Always report these.
            LOGGER.error(event.getPath().toString(), exception);
        }

        if (event.getInteraction() instanceof IReplyCallback callback) {
            if (exception instanceof DiscordEmbeddable embeddable) {
                this.answer(callback, embeddable.asEmbed().build(), !embeddable.showToEveryone());
                return;
            }

            EmbedBuilder builder = buildEmbed(event, sentryId);
            this.answer(callback, builder.build(), true);
        }
    }

    /**
     * Called when no {@link InteractionResponseHandler} could be found for the provided object.
     *
     * @param event
     *         The {@link DispatchEvent} that was used to generate the response.
     * @param response
     *         The response object generated.
     */
    @Override
    public <T extends Interaction> void onNoResponseHandlerFound(DispatchEvent<T> event, Object response) {

        if (event.getInteraction() instanceof IReplyCallback callback) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Alors comment dire...");
            builder.setDescription(
                    "Ce que t'as fais, ça a marché, mais la catastrophe est survenue lorsque le résultat devait être affiché.");

            builder.setFooter(String.format("I-Path: %s", event.getPath().toString()));
            this.answer(callback, builder.build(), true);
        }
    }

    private <T extends Interaction & IReplyCallback> void answer(T interaction, MessageEmbed embed, boolean ephemeral) {

        if (interaction.isAcknowledged()) {
            interaction.getHook().editOriginalEmbeds(embed).complete();
        } else {
            interaction.replyEmbeds(embed).setEphemeral(ephemeral).complete();
        }
    }

}
