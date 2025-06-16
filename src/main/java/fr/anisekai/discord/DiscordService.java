package fr.anisekai.discord;

import fr.alexpado.jda.interactions.InteractionExtension;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.impl.interactions.autocomplete.AutocompleteInteractionTargetImpl;
import fr.alexpado.jda.interactions.impl.interactions.button.ButtonInteractionTargetImpl;
import fr.alexpado.jda.interactions.impl.interactions.slash.SlashInteractionTargetImpl;
import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionErrorHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutoCompleteProvider;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutocompleteInteractionTarget;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import fr.alexpado.jda.interactions.meta.OptionMeta;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import fr.anisekai.discord.annotations.InteractAt;
import fr.anisekai.discord.annotations.InteractionBean;
import fr.anisekai.discord.utils.InteractionType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

@Service
public class DiscordService extends ListenerAdapter implements InteractionErrorHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(DiscordService.class);

    private final DiscordConfiguration              configuration;
    private final Map<String, AutoCompleteProvider> completionProviders = new HashMap<>();
    private final InteractionExtension              extension           = new InteractionExtension();
    private       ListableBeanFactory               beanFactory;

    public DiscordService(InteractionService interactionService, DiscordConfiguration configuration) {

        this.configuration = configuration;
        this.extension.useDefaultMapping();
        interactionService.using(this.extension, this.completionProviders);
    }

    public void login(ListableBeanFactory beanFactory) {

        if (this.configuration.getToken().isBlank() || !this.configuration.isEnabled()) {
            LOGGER.info("Discord token is missing or the bot is disabled.");
            return;
        }

        this.beanFactory = beanFactory;

        LOGGER.info("Booting up discord service...");

        JDABuilder builder = JDABuilder.create(
                this.configuration.getToken(),
                GatewayIntent.getIntents(GatewayIntent.DEFAULT)
        );

        LOGGER.info("Registering JDA listeners...");
        Arrays.stream(beanFactory.getBeanNamesForType(ListenerAdapter.class))
              .map(name -> beanFactory.getBean(name, ListenerAdapter.class))
              .forEach(builder::addEventListeners);

        builder.addEventListeners(this.extension);

        try {
            LOGGER.info("Starting up JDA...");
            builder.build();
        } catch (Exception e) {
            LOGGER.error("Unable to start JDA", e);
            Sentry.withScope(scope -> {
                scope.setLevel(SentryLevel.FATAL);
                Sentry.captureException(e);
            });
        }
    }

    private void register(Object object) {

        List<Method> interactionMethods = Arrays.stream(object.getClass().getMethods())
                                                .filter(method -> method.isAnnotationPresent(Interact.class))
                                                .toList();

        for (Method method : interactionMethods) {
            Interact interact = method.getAnnotation(Interact.class);

            //noinspection DataFlowIssue — We are sure we have the interaction here, due to the filtering above.
            List<OptionMeta>            options = Arrays.stream(interact.options()).map(OptionMeta::new).toList();
            Collection<InteractionType> types   = new ArrayList<>();

            LOGGER.debug(" — Found interaction `{}`", interact.name());

            InteractionMeta slash = new InteractionMeta(
                    interact.name(),
                    interact.description(),
                    interact.target(),
                    options,
                    interact.hideAsSlash(),
                    interact.defer(),
                    interact.shouldReply()
            );

            InteractionMeta button = new InteractionMeta(
                    interact.name(),
                    interact.description(),
                    interact.target(),
                    options,
                    interact.hideAsButton(),
                    interact.defer(),
                    interact.shouldReply()
            );

            if (method.isAnnotationPresent(InteractAt.class)) {
                InteractAt interactAt = method.getAnnotation(InteractAt.class);
                //noinspection DataFlowIssue — We are sure we have the interaction here, due to the `if` above.
                types.addAll(Arrays.asList(interactAt.value()));
            } else {
                types.addAll(Arrays.asList(InteractionType.values()));
            }

            if (types.contains(InteractionType.SLASH)) {
                this.extension.getSlashContainer().register(new SlashInteractionTargetImpl(object, method, slash));
                AutocompleteInteractionTarget target = new AutocompleteInteractionTargetImpl(slash);
                this.completionProviders.forEach(target::addCompletionProvider);
                this.extension.getAutocompleteContainer().register(target);
            }

            if (types.contains(InteractionType.BUTTON)) {
                this.extension.getButtonContainer().register(new ButtonInteractionTargetImpl(object, method, button));
            }
        }
    }

    @Override
    public void onReady(ReadyEvent event) {

        LOGGER.info("Scanning for interactions...");
        this.beanFactory.getBeansWithAnnotation(InteractionBean.class).values().forEach(this::register);
        LOGGER.info("Registering commands....");
        this.extension.getSlashContainer()
                      .upsertCommands(event.getJDA().updateCommands())
                      .queue(commands -> LOGGER.info("Successfully updated {} commands", commands.size()));
    }

    private <T extends Interaction> EmbedBuilder getErrorEmbed(DispatchEvent<T> event) {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Une erreur est survenue.");
        builder.setDescription("Une erreur est survenue lors du traitement de cette action. Merci de réessayer.");
        builder.setFooter(event.getPath().toString());
        builder.setColor(Color.RED);
        return builder;
    }

    @Override
    public <T extends Interaction> void handleException(DispatchEvent<T> event, Exception exception) {

        if (event.getInteraction() instanceof IReplyCallback callback) {
            if (exception instanceof DiscordEmbeddable embeddable) {
                LOGGER.info("Interaction '{}' threw an embeddable exception.", event.getPath());
                this.answer(callback, embeddable.asEmbed().build(), !embeddable.showToEveryone());
                return;
            }

            LOGGER.warn("Interaction '{}' threw an exception.", event.getPath());

            Sentry.withScope(scope -> {
                Map<String, Object> discord = new HashMap<>();
                discord.put("path", event.getPath());
                discord.put("interaction", event.getInteraction().getId());
                discord.put("user", event.getInteraction().getUser().getId());
                discord.put(
                        "guild",
                        Optional.ofNullable(event.getInteraction().getGuild()).map(ISnowflake::getId).orElse(null)
                );
                discord.put(
                        "channel",
                        Optional.ofNullable(event.getInteraction().getChannel()).map(ISnowflake::getId).orElse(null)
                );

                scope.setContexts("interaction", discord);
                scope.setContexts("options", event.getOptions());

                Sentry.captureException(exception);
            });

            EmbedBuilder builder = this.getErrorEmbed(event);
            this.answer(callback, builder.build(), true);
        }
    }

    @Override
    public <T extends Interaction> void onNoResponseHandlerFound(DispatchEvent<T> event, Object response) {

        if (event.getInteraction() instanceof IReplyCallback callback) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Alors comment dire...");
            builder.setDescription("La commande a réussi mais son résultat ne peut être affiché. Oups.");
            builder.setFooter(event.getPath().toString());
            builder.setColor(Color.ORANGE);
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
