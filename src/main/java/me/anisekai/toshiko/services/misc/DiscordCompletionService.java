package me.anisekai.toshiko.services.misc;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.enums.AnimeStatus;
import me.anisekai.toshiko.enums.InterestLevel;
import me.anisekai.toshiko.repositories.AnimeRepository;
import me.anisekai.toshiko.repositories.SeasonalSelectionRepository;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class DiscordCompletionService {

    private final AnimeRepository             animeRepository;
    private final SeasonalSelectionRepository seasonalSelectionRepository;

    public DiscordCompletionService(AnimeRepository animeRepository, SeasonalSelectionRepository seasonalSelectionRepository) {

        this.animeRepository             = animeRepository;
        this.seasonalSelectionRepository = seasonalSelectionRepository;
    }

    public List<Command.Choice> animeCompletion(DispatchEvent<CommandAutoCompleteInteraction> event, String name, String completionName, String value) {

        return this.animeRepository
                .findAll()
                .stream()
                .filter(anime -> anime.getName().toLowerCase().contains(value.toLowerCase()))
                .sorted()
                .map(Anime::asChoice)
                .toList();
    }

    public List<Command.Choice> seasonalSelectionCompletion(DispatchEvent<CommandAutoCompleteInteraction> event, String name, String completionName, String value) {

        return this.seasonalSelectionRepository
                .findAll()
                .stream()
                .filter(ss -> ss.getName().toLowerCase().contains(value.toLowerCase()))
                .sorted(Comparator.comparingLong(SeasonalSelection::getId))
                .map(SeasonalSelection::asChoice)
                .toList();
    }

    public List<Command.Choice> interestLevelCompletion(DispatchEvent<CommandAutoCompleteInteraction> event, String name, String completionName, String value) {

        return Stream.of(InterestLevel.values())
                     .filter(level -> level.getDisplayText().toLowerCase().contains(value.toLowerCase()))
                     .map(level -> new Command.Choice(level.getDisplayText(), level.name()))
                     .toList();
    }

    public List<Command.Choice> animeStatusCompletion(DispatchEvent<CommandAutoCompleteInteraction> event, String name, String completionName, String value) {

        return Stream.of(AnimeStatus.values())
                     .filter(status -> status.getDisplay().toLowerCase().contains(value.toLowerCase()))
                     .map(status -> new Command.Choice(status.getDisplay(), status.name()))
                     .toList();
    }


}
