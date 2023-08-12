package me.anisekai.toshiko.services.data;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.entities.SeasonalVoter;
import me.anisekai.toshiko.events.seasonalselection.SeasonalSelectionCreated;
import me.anisekai.toshiko.interfaces.entities.ISeasonalSelection;
import me.anisekai.toshiko.repositories.SeasonalSelectionRepository;
import me.anisekai.toshiko.services.AbstractDataService;
import me.anisekai.toshiko.services.proxy.SeasonalSelectionProxyService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class SeasonalSelectionDataService extends AbstractDataService<SeasonalSelection, Long, ISeasonalSelection, SeasonalSelectionRepository, SeasonalSelectionProxyService> {

    private final AnimeDataService animeService;

    public SeasonalSelectionDataService(SeasonalSelectionProxyService proxy, AnimeDataService animeService) {

        super(proxy);
        this.animeService = animeService;
    }

    public SeasonalSelection open(String name) {

        // First, let's retrieve all data required for creation.
        Set<Anime> animes = new HashSet<>(this.animeService.getSimulcasts());

        return this.getProxy().create(selection -> {
            selection.setName(name);
            selection.setAnimes(animes);
            selection.setVoters(Collections.emptySet());
            selection.setVotes(Collections.emptySet());
        }, SeasonalSelectionCreated::new);
    }

    public boolean canCloseSafely(ISeasonalSelection selection) {


        int totalVoteAmount   = selection.getVoters().stream().mapToInt(SeasonalVoter::getAmount).sum();
        int currentVoteAmount = selection.getVotes().size();

        return currentVoteAmount == totalVoteAmount;
    }

    public SeasonalSelection close(long id) {

        return this.mod(id, selection -> selection.setClosed(true));
    }

}
