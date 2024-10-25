package me.anisekai.modules.shizue.services.proxy;

import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.modules.shizue.entities.SeasonalVoter;
import me.anisekai.modules.shizue.entities.keys.SeasonalVoterKey;
import me.anisekai.modules.shizue.exceptions.seasonalvoter.SeasonalVoterNotFoundException;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalVoter;
import me.anisekai.modules.shizue.repositories.SeasonalVoterRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
public class SeasonalVoterProxyService extends ProxyService<SeasonalVoter, SeasonalVoterKey, ISeasonalVoter, SeasonalVoterRepository> {

    public SeasonalVoterProxyService(ApplicationEventPublisher publisher, SeasonalVoterRepository repository) {

        super(publisher, repository, SeasonalVoter::new);
    }

    @Override
    public SeasonalVoter getEntity(Function<SeasonalVoterRepository, Optional<SeasonalVoter>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(SeasonalVoterNotFoundException::new);
    }

}
