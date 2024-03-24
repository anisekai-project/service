package me.anisekai.modules.shizue.services.proxy;

import me.anisekai.api.persistence.UpsertResult;
import me.anisekai.api.persistence.helpers.ProxyService;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.shizue.entities.SeasonalSelection;
import me.anisekai.modules.shizue.entities.SeasonalVote;
import me.anisekai.modules.shizue.entities.keys.UserAnimeVoteAssocKey;
import me.anisekai.modules.shizue.events.seasonalvote.SeasonalVoteCreatedEvent;
import me.anisekai.modules.shizue.exceptions.seasonalvote.SeasonalVoteNotFoundException;
import me.anisekai.modules.shizue.interfaces.entities.ISeasonalVote;
import me.anisekai.modules.shizue.repositories.SeasonalVoteRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class SeasonalVoteProxyService extends ProxyService<SeasonalVote, UserAnimeVoteAssocKey, ISeasonalVote, SeasonalVoteRepository> {


    public SeasonalVoteProxyService(ApplicationEventPublisher publisher, SeasonalVoteRepository repository) {

        super(publisher, repository, SeasonalVote::new);
    }

    @Override
    public SeasonalVote getEntity(Function<SeasonalVoteRepository, Optional<SeasonalVote>> selector) {

        return selector.apply(this.getRepository()).orElseThrow(SeasonalVoteNotFoundException::new);
    }

    public UpsertResult<SeasonalVote> upsert(SeasonalSelection selection, DiscordUser user, Anime anime, Consumer<ISeasonalVote> consumer) {

        UserAnimeVoteAssocKey id = new UserAnimeVoteAssocKey(selection, anime, user);

        return this.upsert(repository -> repository.findById(id), vote -> {
            vote.setAnime(anime);
            vote.setUser(user);
            vote.setSeasonalSelection(selection);
            consumer.accept(vote);
        }, SeasonalVoteCreatedEvent::new);
    }

}
