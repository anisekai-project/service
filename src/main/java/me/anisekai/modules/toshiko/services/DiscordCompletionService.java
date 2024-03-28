package me.anisekai.modules.toshiko.services;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import me.anisekai.modules.freya.services.DiskService;
import me.anisekai.modules.linn.enums.AnimeStatus;
import me.anisekai.modules.linn.repositories.AnimeRepository;
import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.enums.BroadcastFrequency;
import me.anisekai.modules.shizue.enums.InterestLevel;
import me.anisekai.modules.shizue.repositories.SeasonalSelectionRepository;
import me.anisekai.modules.toshiko.Texts;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class DiscordCompletionService {

    private final DiskService                 diskService;
    private final AnimeRepository             animeRepository;
    private final SeasonalSelectionRepository seasonalSelectionRepository;

    public DiscordCompletionService(DiskService diskService, AnimeRepository animeRepository, SeasonalSelectionRepository seasonalSelectionRepository) {

        this.diskService                 = diskService;
        this.animeRepository             = animeRepository;
        this.seasonalSelectionRepository = seasonalSelectionRepository;
    }

    public List<Command.Choice> diskGroupCompletion(DispatchEvent<CommandAutoCompleteInteraction> event, String name, String completionName, String value) {

        return this.diskService
                .getDatabase()
                .stream()
                .filter(diskAnime -> diskAnime.getName().toLowerCase().contains(value.toLowerCase()))
                .sorted()
                .map(diskAnime -> new Command.Choice(diskAnime.getName(), diskAnime.getUuid().toString()))
                .toList();
    }

    public List<Command.Choice> diskAnimeCompletion(DispatchEvent<CommandAutoCompleteInteraction> event, String name, String completionName, String value) {

        return this.diskService
                .getDatabase()
                .stream()
                .sorted()
                .flatMap(diskAnime -> diskAnime.getGroups().stream())
                .filter(diskGroup -> diskGroup.getName().toLowerCase().contains(value.toLowerCase()))
                .map(diskGroup -> new Command.Choice(diskGroup.getName(), diskGroup.getUuid().toString()))
                .toList();
    }

    public List<Command.Choice> animeCompletion(DispatchEvent<CommandAutoCompleteInteraction> event, String name, String completionName, String value) {

        return this.animeRepository
                .findAll()
                .stream()
                .filter(anime -> anime.getName().toLowerCase().contains(value.toLowerCase()))
                .sorted()
                .map(anime -> new Command.Choice(Texts.truncate(String.format(
                        "%s %s",
                        anime.getStatus().getIcon(),
                        anime.getName()
                ), 100), anime.getId()))
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

    public List<Command.Choice> frequencyCompletion(DispatchEvent<CommandAutoCompleteInteraction> event, String name, String completionName, String value) {

        return Stream.of(BroadcastFrequency.values())
                     .filter(frequency -> frequency.getDisplayName().toLowerCase().contains(value.toLowerCase()))
                     .map(frequency -> new Command.Choice(frequency.getDisplayName(), frequency.name()))
                     .toList();
    }

}
