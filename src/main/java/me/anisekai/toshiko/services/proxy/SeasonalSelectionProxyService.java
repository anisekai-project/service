package me.anisekai.toshiko.services.proxy;

import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.exceptions.seasonalselection.SeasonalSelectionNotFoundException;
import me.anisekai.toshiko.interfaces.entities.ISeasonalSelection;
import me.anisekai.toshiko.repositories.SeasonalSelectionRepository;
import me.anisekai.toshiko.services.AbstractProxyService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class SeasonalSelectionProxyService extends AbstractProxyService<SeasonalSelection, Long, ISeasonalSelection, SeasonalSelectionRepository> {

    public SeasonalSelectionProxyService(ApplicationEventPublisher publisher, SeasonalSelectionRepository repository) {

        super(publisher, repository, SeasonalSelection::new);
    }

    @Override
    public SeasonalSelection getEntity(Function<SeasonalSelectionRepository, Optional<SeasonalSelection>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(SeasonalSelectionNotFoundException::new);
    }

}
