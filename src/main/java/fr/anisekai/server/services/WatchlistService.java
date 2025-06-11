package fr.anisekai.server.services;

import fr.anisekai.wireless.remote.enums.AnimeList;
import fr.anisekai.wireless.remote.enums.AnimeList.Property;
import fr.anisekai.wireless.remote.interfaces.WatchlistEntity;
import fr.anisekai.server.entities.Watchlist;
import fr.anisekai.server.entities.adapters.WatchlistEventAdapter;
import fr.anisekai.server.persistence.DataService;
import fr.anisekai.server.proxy.WatchlistProxy;
import fr.anisekai.server.repositories.WatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class WatchlistService extends DataService<Watchlist, AnimeList, WatchlistEventAdapter, WatchlistRepository, WatchlistProxy> {


    public WatchlistService(WatchlistProxy proxy) {

        super(proxy);
    }

    /**
     * Create a {@link Watchlist} for the provided {@link AnimeList}.
     *
     * @param list
     *         The {@link AnimeList} from which the {@link Watchlist} will be created.
     *
     * @return A {@link Watchlist}
     */
    private Watchlist create(AnimeList list) {

        return this.getProxy().create(entity -> {
            entity.setId(list);
        });
    }

    /**
     * Create all {@link Watchlist} matching any {@link AnimeList} having the {@link AnimeList.Property#SHOW} property,
     * in the order they have been declared in the enum.
     *
     * @return A {@link List} of all created {@link Watchlist}.
     */
    public List<Watchlist> create() {

        List<Watchlist> all = this.getProxy().getRepository().findAll();

        if (!all.isEmpty()) {
            throw new IllegalStateException("You cannot use create() when there are existing watchlists");
        }

        return AnimeList.collect(Property.SHOW).stream()
                        .sorted(Comparator.comparingInt(Enum::ordinal))
                        .map(this::create)
                        .toList();
    }

    /**
     * Delete all {@link Watchlist} and re-create them using {@link #create()}.
     * <p>
     * <b>Note:</b> Corresponding Discord message ({@link WatchlistEntity#getMessageId()}) will not be deleted.
     *
     * @return A {@link List} of all created {@link Watchlist}.
     */
    public List<Watchlist> reset() {

        this.getProxy().getRepository().deleteAll();
        return this.create();
    }

}
