package me.anisekai.toshiko.services.proxy;

import me.anisekai.toshiko.entities.SeasonalVoter;
import me.anisekai.toshiko.entities.keys.SeasonalVoterKey;
import me.anisekai.toshiko.exceptions.seasonalvoter.SeasonalVoterNotFoundException;
import me.anisekai.toshiko.interfaces.entities.ISeasonalVoter;
import me.anisekai.toshiko.repositories.SeasonalVoterRepository;
import me.anisekai.toshiko.services.AbstractProxyService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class SeasonalVoterProxyService extends AbstractProxyService<SeasonalVoter, SeasonalVoterKey, ISeasonalVoter, SeasonalVoterRepository> {

    public SeasonalVoterProxyService(ApplicationEventPublisher publisher, SeasonalVoterRepository repository) {

        super(publisher, repository, SeasonalVoter::new);
    }

    @Override
    public SeasonalVoter getEntity(Function<SeasonalVoterRepository, Optional<SeasonalVoter>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(SeasonalVoterNotFoundException::new);
    }

}
