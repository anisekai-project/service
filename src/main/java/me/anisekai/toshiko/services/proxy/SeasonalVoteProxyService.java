package me.anisekai.toshiko.services.proxy;

import me.anisekai.toshiko.entities.Anime;
import me.anisekai.toshiko.entities.DiscordUser;
import me.anisekai.toshiko.entities.SeasonalSelection;
import me.anisekai.toshiko.entities.SeasonalVote;
import me.anisekai.toshiko.entities.keys.UserAnimeVoteAssocKey;
import me.anisekai.toshiko.events.seasonalvote.SeasonalVoteCreatedEvent;
import me.anisekai.toshiko.exceptions.seasonalvote.SeasonalVoteNotFoundException;
import me.anisekai.toshiko.helpers.UpsertResult;
import me.anisekai.toshiko.interfaces.entities.ISeasonalVote;
import me.anisekai.toshiko.repositories.SeasonalVoteRepository;
import me.anisekai.toshiko.services.AbstractProxyService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class SeasonalVoteProxyService extends AbstractProxyService<SeasonalVote, UserAnimeVoteAssocKey, ISeasonalVote, SeasonalVoteRepository> {


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
