package fr.anisekai.library.tasks.executors;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.anisekai.library.Library;
import fr.anisekai.sanctum.AccessScope;
import fr.anisekai.sanctum.interfaces.isolation.IsolationSession;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Track;
import fr.anisekai.server.services.EpisodeService;
import fr.anisekai.server.services.TrackService;
import fr.anisekai.server.tasking.TaskExecutor;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.api.json.validation.JsonObjectRule;
import fr.anisekai.wireless.api.media.MediaFile;
import fr.anisekai.wireless.api.media.MediaStream;
import fr.anisekai.wireless.api.media.bin.FFMpeg;
import fr.anisekai.wireless.api.media.enums.Codec;
import fr.anisekai.wireless.api.media.enums.CodecType;
import fr.anisekai.wireless.api.media.enums.Disposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MediaImportTask implements TaskExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaImportTask.class);

    private static final Codec VIDEO_CODEC = Codec.H264;
    private static final Codec AUDIO_CODEC = Codec.AAC;

    public static final String OPTION_SOURCE  = "source";
    public static final String OPTION_EPISODE = "episode";
    public static final String OPTION_DELETE  = "delete";

    private final Library        library;
    private final TrackService   trackService;
    private final EpisodeService episodeService;

    public MediaImportTask(Library library, TrackService trackService, EpisodeService episodeService) {

        this.library        = library;
        this.trackService   = trackService;
        this.episodeService = episodeService;
    }

    @Override
    public void validateParams(AnisekaiJson params) {

        params.validate(
                new JsonObjectRule(OPTION_SOURCE, true, String.class),
                new JsonObjectRule(OPTION_EPISODE, true, int.class, Integer.class, long.class, Long.class),
                new JsonObjectRule(OPTION_DELETE, false, boolean.class, Boolean.class)
        );
    }

    @Override
    public void execute(ITimedAction timer, AnisekaiJson params) throws IOException, InterruptedException {

        Path    source  = Path.of(params.getString(OPTION_SOURCE));
        Episode episode = this.episodeService.fetch(params.getLong(OPTION_EPISODE));
        boolean delete  = params.optBoolean(OPTION_DELETE, false);

        String taskName = String.format("%s:%s", episode.getAnime().getId(), episode.getNumber());

        MediaFile media = MediaFile.of(source);

        AccessScope chunksScope   = new AccessScope(Library.CHUNKS, episode);
        AccessScope subtitleScope = new AccessScope(Library.SUBTITLES, episode);

        Set<AccessScope> scopes = Set.of(chunksScope, subtitleScope);

        try (IsolationSession context = this.library.createIsolation(scopes)) {
            Path temporary    = context.requestTemporaryFile("mkv");
            Path chunkStorage = context.resolve(chunksScope);
            Path subStorage   = context.resolve(subtitleScope);

            if (!Files.exists(chunkStorage)) {
                Files.createDirectories(chunkStorage);
            }

            if (!Files.exists(subStorage)) {
                Files.createDirectories(subStorage);
            }

            LOGGER.info("[{}] Converting media {}...", taskName, source);
            FFMpeg.convert(media)
                  .video(VIDEO_CODEC)
                  .audio(AUDIO_CODEC)
                  .noSubtitle()
                  .file(temporary)
                  .timeout(3, TimeUnit.HOURS)
                  .run();
            LOGGER.info("[{}] File converted to {}", taskName, temporary);

            MediaFile converted = MediaFile.of(temporary);

            LOGGER.info("[{}] Creating MPD Meta with chunks...", taskName);
            FFMpeg.mdp(converted)
                  .into(chunkStorage)
                  .as("meta.mpd")
                  .timeout(5, TimeUnit.MINUTES)
                  .run();
            LOGGER.info("[{}] MPD Meta created.", taskName);

            LOGGER.info("[{}] Handling tracks...", taskName);
            Map<MediaStream, String> nameMapping = new HashMap<>();

            for (MediaStream stream : media.getStreams()) {
                Track track = this.trackService.getProxy().create(entity -> {
                    entity.setEpisode(episode);
                    entity.setName("Track %s".formatted(stream.getId()));
                    entity.setLabel(stream.getMetadata().get("title"));
                    entity.setCodec(stream.getCodec());
                    entity.setLanguage(stream.getMetadata().get("language"));
                    entity.setForced(stream.getDispositions().contains(Disposition.FORCED));
                });

                if (stream.getCodec().getType() == CodecType.SUBTITLE) {
                    String filename = String.format("%s.%s", track.getId(), stream.getCodec().getExtension());
                    nameMapping.put(stream, filename);
                }
            }

            FFMpeg.convert(media)
                  .noVideo()
                  .noAudio()
                  .subtitle(Codec.SSA)
                  .into(subStorage)
                  .split((stream, codec) -> nameMapping.get(stream))
                  .timeout(1, TimeUnit.HOURS)
                  .run();

            LOGGER.info("[{}] Committing files to library...", taskName);
            context.commit();
            this.episodeService.mod(episode.getId(), entity -> entity.setReady(true));
            LOGGER.info("[{}] The media file has been imported.", taskName);
        }

        if (delete) {
            Files.delete(source);
        }
    }

}
