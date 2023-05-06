package me.anisekai.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Choice;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.toshiko.data.Task;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.helpers.InteractionBean;
import me.anisekai.toshiko.messages.AnimeNightMessages;
import me.anisekai.toshiko.messages.GeneralEnglishMessage;
import me.anisekai.toshiko.messages.GeneralFrenchMessage;
import me.anisekai.toshiko.messages.responses.SimpleResponse;
import me.anisekai.toshiko.services.misc.TaskService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@InteractionBean
public class MessageInteractions {

    private final TaskService taskService;

    public MessageInteractions(TaskService taskService) {

        this.taskService = taskService;
    }

    // <editor-fold desc="@ rules ─ Show the rules">
    @Interact(
            name = "rules",
            description = "Affiche les règles",
            options = {
                    @Option(
                            name = "num",
                            description = "Affiche une règle particulière",
                            type = OptionType.INTEGER,
                            choices = {
                                    @Choice(
                                            id = "1",
                                            display = "1. Bon sens et courtoisie | Common Sense and courtesy"
                                    ),
                                    @Choice(
                                            id = "2",
                                            display = "2. Ambiance bon enfant | Friendly Atmosphere"
                                    ),
                                    @Choice(
                                            id = "3",
                                            display = "3. Pas de pub | No advertisement"
                                    )
                            }
                    )
            }
    )
    public SlashResponse showRules(@Param("num") Long num, MessageChannel channel) {

        if (num == null) {
            this.taskService.queue(new Task() {

                @Override
                public String getName() {

                    return "MSG:RULES";
                }

                @Override
                public void onFinished() {

                }

                @Override
                public void onException(Exception e) {

                }

                @Override
                public void run() throws Exception {

                    channel.sendMessage(MessageCreateData.fromEmbeds(
                            GeneralFrenchMessage.getRulesEmbed().build(),
                            GeneralEnglishMessage.getRulesEmbed().build()
                    )).complete();
                }
            });
            return new SimpleResponse("Les messages vont être envoyés.", false, true);
        }

        return switch (num.intValue()) {
            case 1 -> new SimpleResponse(Arrays.asList(
                    new EmbedBuilder().addField(GeneralFrenchMessage.getRuleOne()),
                    new EmbedBuilder().addField(GeneralEnglishMessage.getRuleOne())
            ), false, false);
            case 2 -> new SimpleResponse(Arrays.asList(
                    new EmbedBuilder().addField(GeneralFrenchMessage.getRuleTwo()),
                    new EmbedBuilder().addField(GeneralEnglishMessage.getRuleTwo())
            ), false, false);
            case 3 -> new SimpleResponse(Arrays.asList(
                    new EmbedBuilder().addField(GeneralFrenchMessage.getRuleThree()),
                    new EmbedBuilder().addField(GeneralEnglishMessage.getRuleThree())
            ), false, false);
            default -> new SimpleResponse("Numéro de règle inconnu.", false, true);
        };
    }
    // </editor-fold>

    // <editor-fold desc="@ anisekai ─ Display help message about anime nights">
    @Interact(
            name = "anisekai",
            description = "Affiche le message d'aide à propos des soirées anime.",
            defer = true,
            options = {
                    @Option(
                            name = "category",
                            description = "Catégorie de l'aide",
                            type = OptionType.STRING,
                            choices = {
                                    @Choice(
                                            id = "vote",
                                            display = "Le système de vote"
                                    ),
                                    @Choice(
                                            id = "activity",
                                            display = "Personne 'active'"
                                    ),
                                    @Choice(
                                            id = "nights",
                                            display = "Les séances de visionnage"
                                    ),
                                    @Choice(
                                            id = "reschedule",
                                            display = "Absence ou retard à une séance de visionnage"
                                    ),
                                    @Choice(
                                            id = "simulcast",
                                            display = "Les simulcasts"
                                    ),
                                    @Choice(
                                            id = "import",
                                            display = "Ajouter un anime"
                                    ),
                                    @Choice(
                                            id = "bot",
                                            display = "Utiliser le bot"
                                    )
                            }
                    )
            }
    )
    public SlashResponse animeNightInformation(DiscordUser user, @Param("category") String category, MessageChannel channel) {

        if (category == null) {
            if (!user.isAdmin()) {
                return new SimpleResponse("Veuillez choisir une catégorie spécifique (l'affichage de l'aide complète ne peut être faite que par un administrateur).", false, false);
            }

            // Here we go boys
            this.send("ANISEKAI:NIGHTS:INTRODUCTION", channel, AnimeNightMessages.getIntroduction());
            this.send("ANISEKAI:NIGHTS:VOTE", channel, AnimeNightMessages.getVoteSystem());
            this.send("ANISEKAI:NIGHTS:ACTIVITY", channel, AnimeNightMessages.getActivity());
            this.send("ANISEKAI:NIGHTS:NIGHTS", channel, AnimeNightMessages.getAnimeNights());
            this.send("ANISEKAI:NIGHTS:RESCHEDULE", channel, AnimeNightMessages.getDelayOrCancel());
            this.send("ANISEKAI:NIGHTS:SIMULCAST", channel, AnimeNightMessages.getSimulcast());
            this.send("ANISEKAI:NIGHTS:IMPORT", channel, AnimeNightMessages.getAnimeImport());
            this.send("ANISEKAI:NIGHTS:BOT", channel, AnimeNightMessages.getBotUsage());

            return new SimpleResponse("**OK !**", false, true);
        }

        return switch (category) {
            case "vote" -> new SimpleResponse(AnimeNightMessages.getVoteSystem(), false, false);
            case "activity" -> new SimpleResponse(AnimeNightMessages.getActivity(), false, false);
            case "nights" -> new SimpleResponse(AnimeNightMessages.getAnimeNights(), false, false);
            case "reschedule" -> new SimpleResponse(AnimeNightMessages.getDelayOrCancel(), false, false);
            case "simulcast" -> new SimpleResponse(AnimeNightMessages.getSimulcast(), false, false);
            case "import" -> new SimpleResponse(AnimeNightMessages.getAnimeImport(), false, false);
            case "bot" -> new SimpleResponse(AnimeNightMessages.getBotUsage(), false, false);
            default -> new SimpleResponse("**Oups:** Je ne connais pas cette catégorie d'aide :(", false, true);
        };

    }
    // </editor-fold>


    private void send(String name, MessageChannel channel, EmbedBuilder builder) {

        this.taskService.queue(new Task() {

            @Override
            public String getName() {

                return name;
            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onException(Exception e) {

            }

            @Override
            public void run() throws Exception {
                channel.sendMessageEmbeds(builder.build()).complete();
            }
        });
    }
}
