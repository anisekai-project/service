package me.anisekai.modules.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.api.persistence.UpsertResult;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.services.data.AnimeDataService;
import me.anisekai.modules.shizue.entities.Interest;
import me.anisekai.modules.shizue.enums.InterestLevel;
import me.anisekai.modules.shizue.services.data.InterestDataService;
import me.anisekai.modules.toshiko.Texts;
import me.anisekai.modules.toshiko.annotations.InteractionBean;
import me.anisekai.modules.toshiko.messages.embeds.InterestResponse;
import me.anisekai.modules.toshiko.messages.responses.SimpleResponse;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.springframework.stereotype.Component;

@Component
@InteractionBean
public class InterestInteraction {

    private final InterestDataService service;
    private final AnimeDataService    animeService;

    public InterestInteraction(InterestDataService service, AnimeDataService animeService) {

        this.service      = service;
        this.animeService = animeService;
    }

    // <editor-fold desc="@ interest/set [anime: integer, interest: string]">
    @Interact(
            name = "interest/set",
            description = Texts.ANIME_INTEREST__DESCRIPTION,
            options = {
                    @Option(
                            name = "anime",
                            description = Texts.ANIME_INTEREST__OPTION_NAME,
                            type = OptionType.INTEGER,
                            required = true,
                            autoComplete = true
                    ),
                    @Option(
                            name = "interest",
                            description = Texts.ANIME_INTEREST__OPTION_LEVEL,
                            type = OptionType.STRING,
                            required = true,
                            autoComplete = true
                    )
            }
    )
    public SlashResponse runInterestSet(
            Interaction interaction,
            DiscordUser discordUser,
            @Param("anime") long animeId,
            @Param("interest") String interestName
    ) {

        if (discordUser.getEmote() == null) {
            return new SimpleResponse(
                    "Avant de pouvoir vote pour un anime, tu dois définir ton icône de vote. (`/profile`)",
                    false,
                    true
            );
        }

        Anime         anime = this.animeService.fetch(animeId);
        InterestLevel level = InterestLevel.from(interestName);

        return this.service.setInterest(discordUser, anime, level)
                           .map(result -> this.asResponse(interaction, result))
                           .orElseGet(() -> this.asResponse(interaction));
    }

    private SlashResponse asResponse(Interaction interaction, UpsertResult<Interest> result) {

        return new InterestResponse(result.result(), interaction instanceof SlashCommandInteraction);
    }

    private SlashResponse asResponse(Interaction interaction) {

        return new SimpleResponse(
                "Ton niveau d'intérêt reste inchangé.",
                false,
                interaction instanceof ButtonInteraction
        );
    }
    // </editor-fold>

}
