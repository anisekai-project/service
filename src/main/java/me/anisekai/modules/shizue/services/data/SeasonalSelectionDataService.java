package me.anisekai.modules.shizue.services.data;

import me.anisekai.api.persistence.helpers.DataService;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.services.data.AnimeDataService;
import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.entities.SeasonalVoter;
import me.anisekai.modules.shizue.events.seasonalselection.SeasonalSelectionCreated;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalSelection;
import me.anisekai.modules.shizue.repositories.SeasonalSelectionRepository;
import me.anisekai.modules.shizue.services.proxy.SeasonalSelectionProxyService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class SeasonalSelectionDataService extends DataService<SeasonalSelection, Long, ISeasonalSelection, SeasonalSelectionRepository, SeasonalSelectionProxyService> {

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
