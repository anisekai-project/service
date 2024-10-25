package me.anisekai.modules.shizue.services.proxy;

import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.exceptions.seasonalselection.SeasonalSelectionNotFoundException;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalSelection;
import me.anisekai.modules.shizue.repositories.SeasonalSelectionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class SeasonalSelectionProxyService extends ProxyService<SeasonalSelection, Long, ISeasonalSelection, SeasonalSelectionRepository> {

    public SeasonalSelectionProxyService(ApplicationEventPublisher publisher, SeasonalSelectionRepository repository) {

        super(publisher, repository, SeasonalSelection::new);
    }

    @Override
    public SeasonalSelection getEntity(Function<SeasonalSelectionRepository, Optional<SeasonalSelection>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(SeasonalSelectionNotFoundException::new);
    }

}
