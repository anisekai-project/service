package fr.anisekai.server.services;

import fr.anisekai.server.entities.Anime;
import fr.anisekai.server.entities.Selection;
import fr.anisekai.server.entities.adapters.SelectionEventAdapter;
import fr.anisekai.server.persistence.DataService;
import fr.anisekai.server.proxy.SelectionProxy;
import fr.anisekai.server.repositories.SelectionRepository;
import fr.anisekai.wireless.remote.enums.AnimeSeason;
import fr.anisekai.wireless.remote.enums.SelectionStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;

@Service
public class SelectionService extends DataService<Selection, Long, SelectionEventAdapter, SelectionRepository, SelectionProxy> {

    private static final long DEFAULT_TOTAL_VOTE = 8;


    private final AnimeService animeService;

    public SelectionService(SelectionProxy proxy, AnimeService animeService) {

        super(proxy);
        this.animeService = animeService;
    }

    public Selection createSelection() {

        return this.createSelection(DEFAULT_TOTAL_VOTE);
    }

    public Selection createSelection(long amount) {

        // Retrieving data necessary for the selection
        List<Anime> simulcasts = this.animeService.getSimulcastsAvailable();

        // Adding one month here ensure we fall under the right season and year for the selection, hopefully.
        ZonedDateTime now    = ZonedDateTime.now().plusMonths(1);
        AnimeSeason   season = AnimeSeason.fromDate(now);

        Selection selection = this.getProxy().create(entity -> {
            entity.setSeason(season);
            entity.setStatus(SelectionStatus.OPEN);
            entity.setYear(now.getYear());
            entity.setAnimes(new HashSet<>(simulcasts));
        });

        if (simulcasts.size() <= amount) {
            // Auto-close selection. Events should handle the anime watchlist update.
            return this.mod(selection.getId(), entity -> entity.setStatus(SelectionStatus.AUTO_CLOSED));
        }

        return selection;
    }

}
