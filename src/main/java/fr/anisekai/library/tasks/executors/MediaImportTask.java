package fr.anisekai.library.tasks.executors;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.anisekai.library.Library;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Torrent;
import fr.anisekai.server.entities.TorrentFile;
import fr.anisekai.server.entities.Track;
import fr.anisekai.server.services.EpisodeService;
import fr.anisekai.server.services.TorrentFileService;
import fr.anisekai.server.services.TorrentService;
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
import fr.anisekai.wireless.api.storage.containers.AccessScope;
import fr.anisekai.wireless.api.storage.interfaces.StorageIsolationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MediaImportTask implements TaskExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaImportTask.class);

    private static final Codec VIDEO_CODEC = Codec.H264;
    private static final Codec AUDIO_CODEC = Codec.AAC;

    public static final String OPTION_TORRENT = "torrent";

    private final Library            library;
    private final TrackService       trackService;
    private final TorrentService     torrentService;
    private final TorrentFileService torrentFileService;
    private final EpisodeService     episodeService;

    public MediaImportTask(Library library, TrackService trackService, TorrentService torrentService, TorrentFileService torrentFileService, EpisodeService episodeService) {

        this.library            = library;
        this.trackService       = trackService;
        this.torrentService     = torrentService;
        this.torrentFileService = torrentFileService;
        this.episodeService     = episodeService;
    }

    @Override
    public void validateParams(AnisekaiJson params) {

        params.validate(
                new JsonObjectRule(OPTION_TORRENT, true, String.class)
        );
    }

    @Override
    public void execute(ITimedAction timer, AnisekaiJson params) throws IOException, InterruptedException {

        Torrent           torrent = this.torrentService.fetch(params.getString(OPTION_TORRENT));
        List<TorrentFile> files   = this.torrentFileService.getFiles(torrent);

        for (TorrentFile file : files) {

            Path      downloaded = this.detectFile(file);
            Episode   episode    = file.getEpisode();
            MediaFile media      = MediaFile.of(downloaded);

            AccessScope chunksScope   = new AccessScope(Library.CHUNKS, episode);
            AccessScope subtitleScope = new AccessScope(Library.SUBTITLES, episode);

            Set<AccessScope> scopes = Set.of(chunksScope, subtitleScope);

            try (StorageIsolationContext context = this.library.createIsolation(scopes)) {
                Path temporary    = context.requestTemporaryFile("mkv");
                Path chunkStorage = context.resolveScope(chunksScope);
                Path subStorage   = context.resolveScope(subtitleScope);

                LOGGER.info("[{}:{}] Converting Video/Audio...", torrent.getId(), file.getIndex());
                FFMpeg.convert(media)
                      .video(VIDEO_CODEC)
                      .audio(AUDIO_CODEC)
                      .noSubtitle()
                      .file(temporary)
                      .timeout(3, TimeUnit.HOURS)
                      .run();
                LOGGER.info("[{}:{}] File converted to {}", torrent.getId(), file.getIndex(), temporary);

                MediaFile converted = MediaFile.of(temporary);

                LOGGER.info("[{}:{}] Creating MPD Meta with chunks...", torrent.getId(), file.getIndex());
                FFMpeg.mdp(converted)
                      .into(chunkStorage)
                      .as("meta.mpd")
                      .timeout(5, TimeUnit.MINUTES)
                      .run();
                LOGGER.info("[{}:{}] MPD Meta created.", torrent.getId(), file.getIndex());

                LOGGER.info("[{}:{}] Handling tracks...", torrent.getId(), file.getIndex());
                Map<MediaStream, String> nameMapping = new HashMap<>();

                for (MediaStream stream : media.getStreams()) {
                    Track track = this.trackService.getProxy().create(entity -> {
                        entity.setEpisode(episode);
                        entity.setName("Track %s".formatted(stream.getId()));
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
                      .copySubtitle()
                      .into(subStorage)
                      .split((stream, codec) -> nameMapping.get(stream))
                      .timeout(1, TimeUnit.HOURS)
                      .run();

                LOGGER.info("[{}:{}] Committing files to library...", torrent.getId(), file.getIndex());
                context.commit();
                this.episodeService.mod(episode.getId(), entity -> entity.setReady(true));
                LOGGER.info("[{}:{}] The torrent file has been imported.", torrent.getId(), file.getIndex());
            }
        }
    }

    private Path detectFile(TorrentFile file) {

        return this.library
                .findDownload(file)
                .orElseThrow(() -> new IllegalStateException(
                        String.format(
                                "Could not determine path to file %s of torrent %s",
                                file.getIndex(),
                                file.getTorrent().getId()
                        )));
    }

}
