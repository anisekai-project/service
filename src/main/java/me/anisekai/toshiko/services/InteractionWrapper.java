package me.anisekai.toshiko.services;

import fr.alexpado.jda.interactions.InteractionExtension;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.impl.interactions.autocomplete.AutocompleteInteractionTargetImpl;
import fr.alexpado.jda.interactions.impl.interactions.button.ButtonInteractionTargetImpl;
import fr.alexpado.jda.interactions.impl.interactions.slash.SlashInteractionTargetImpl;
import fr.alexpado.jda.interactions.interfaces.interactions.Injection;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutocompleteInteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.button.ButtonInteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.slash.SlashInteractionTarget;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import fr.alexpado.jda.interactions.meta.OptionMeta;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.repositories.AnimeRepository;
import me.anisekai.toshiko.repositories.UserRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class InteractionWrapper {

    private final AnimeRepository      animeRepository;
    private final UserRepository       userRepository;
    private final ListableBeanFactory  beanFactory;
    private final InteractionExtension extension;

    public InteractionWrapper(AnimeRepository animeRepository, UserRepository userRepository, ListableBeanFactory beanFactory) {

        this.animeRepository = animeRepository;
        this.userRepository  = userRepository;
        this.beanFactory     = beanFactory;
        this.extension       = new InteractionExtension();

        this.extension.getSlashContainer().addClassMapping(DiscordUser.class, this.entityUserMapper());
        this.extension.getButtonContainer().addClassMapping(DiscordUser.class, this.entityUserMapper());
    }

    private void hook(JDA jda, Supplier<CommandListUpdateAction> action) {

        this.beanFactory.getBeansWithAnnotation(InteractionBean.class)
                        .values()
                        .forEach(this::register);

        jda.addEventListener(this.extension);
        this.extension.useDefaultMapping();
        this.extension.getSlashContainer().upsertCommands(action.get()).complete();
    }

    public void hook(JDA jda) {

        this.hook(jda, jda::updateCommands);
    }

    public void hook(Guild guild) {

        this.hook(guild.getJDA(), guild::updateCommands);
    }

    public void register(Object obj) {

        for (Method method : obj.getClass().getMethods()) {
            if (method.isAnnotationPresent(Interact.class)) {
                Interact annotation = method.getAnnotation(Interact.class);

                List<OptionMeta> options = Arrays.stream(annotation.options()).map(OptionMeta::new).toList();

                InteractionMeta slashMeta = new InteractionMeta(
                        annotation.name(),
                        annotation.description(),
                        annotation.target(),
                        options,
                        annotation.hideAsSlash(),
                        false,
                        true
                );

                InteractionMeta buttonMeta = new InteractionMeta(
                        annotation.name(),
                        annotation.description(),
                        annotation.target(),
                        options,
                        annotation.hideAsButton(),
                        false,
                        annotation.shouldReply()
                );

                SlashInteractionTarget        slash      = new SlashInteractionTargetImpl(obj, method, slashMeta);
                ButtonInteractionTarget       button     = new ButtonInteractionTargetImpl(obj, method, buttonMeta);
                AutocompleteInteractionTarget completion = new AutocompleteInteractionTargetImpl(slashMeta);

                completion.addCompletionProvider("anime", (event, name, value) ->
                        this.animeRepository
                                .findAll()
                                .stream()
                                .filter(anime -> anime.getName().toLowerCase().contains(value.toLowerCase()))
                                .sorted()
                                .map(anime -> new Command.Choice(anime.getName(), anime.getId()))
                                .toList()
                );

                completion.addCompletionProvider("interest", (event, name, value) ->
                        Stream.of(InterestLevel.values())
                              .filter(level -> level.getDisplayText().toLowerCase().contains(value.toLowerCase()))
                              .map(level -> new Command.Choice(level.getDisplayText(), level.name()))
                              .toList()
                );

                completion.addCompletionProvider("status", (event, name, value) ->
                        Stream.of(AnimeStatus.values())
                              .filter(status -> status.getDisplay().toLowerCase().contains(value.toLowerCase()))
                              .map(status -> new Command.Choice(status.getDisplay(), status.name()))
                              .toList()
                );

                this.extension.getSlashContainer().register(slash);
                this.extension.getButtonContainer().register(button);
                this.extension.getAutocompleteContainer().register(completion);
            }
        }
    }

    private <T extends Interaction> Injection<DispatchEvent<T>, DiscordUser> entityUserMapper() {

        return event -> () -> this.userRepository
                .findById(event.getInteraction().getUser().getIdLong())
                .orElseGet(() -> this.userRepository.save(new DiscordUser(event.getInteraction().getUser())));
    }

}
