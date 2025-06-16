package fr.anisekai.library.tasks.executors;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.anisekai.server.services.EpisodeService;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.api.json.validation.JsonObjectRule;
import fr.anisekai.wireless.api.media.MediaFile;
import fr.anisekai.wireless.api.media.MediaStream;
import fr.anisekai.wireless.api.media.bin.FFMpeg;
import fr.anisekai.wireless.api.media.enums.Codec;
import fr.anisekai.library.LibraryService;
import fr.anisekai.server.entities.Episode;
import fr.anisekai.server.entities.Torrent;
import fr.anisekai.server.entities.TorrentFile;
import fr.anisekai.server.entities.Track;
import fr.anisekai.server.services.TorrentFileService;
import fr.anisekai.server.services.TorrentService;
import fr.anisekai.server.services.TrackService;
import fr.anisekai.server.tasking.TaskExecutor;
import fr.anisekai.wireless.api.media.enums.Disposition;
import fr.anisekai.wireless.remote.interfaces.TorrentFileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

public class MediaImportTask implements TaskExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaImportTask.class);

    private static final Codec VIDEO_CODEC = Codec.H264;
    private static final Codec AUDIO_CODEC = Codec.AAC;

    public static final String OPTION_TORRENT = "torrent";

    private final LibraryService     libraryService;
    private final TrackService       trackService;
    private final TorrentService     torrentService;
    private final TorrentFileService torrentFileService;
    private final EpisodeService     episodeService;

    public MediaImportTask(LibraryService libraryService, TrackService trackService, TorrentService torrentService, TorrentFileService torrentFileService, EpisodeService episodeService) {

        this.libraryService     = libraryService;
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

            File      downloaded = this.detectFile(file);
            Episode   episode    = file.getEpisode();
            MediaFile media      = MediaFile.of(downloaded);

            LOGGER.info("[{}:{}] Converting Video/Audio...", torrent.getId(), file.getIndex());
            File tmp = this.libraryService.requestTemporaryFile("mkv");
            FFMpeg.convert(media, VIDEO_CODEC, AUDIO_CODEC, null, tmp, 6);
            LOGGER.info("[{}:{}] File converted to {}", torrent.getId(), file.getIndex(), tmp.getAbsolutePath());

            LOGGER.info("[{}:{}] Creating MPD Meta with chunks...", torrent.getId(), file.getIndex());
            this.libraryService.storeEpisode(episode, tmp);
            this.deleteFile(tmp);
            LOGGER.info("[{}:{}] MPD Meta created.", torrent.getId(), file.getIndex());

            LOGGER.info("[{}:{}] Saving subtitles and generating track entities...", torrent.getId(), file.getIndex());
            Map<MediaStream, File> subs = FFMpeg.explode(media, null, null, Codec.SUBTITLES_COPY, 1);

            for (MediaStream stream : media.getStreams()) {
                Track track = this.trackService.getProxy().create(entity -> {
                    entity.setEpisode(episode);
                    entity.setName("Track %s".formatted(stream.getId()));
                    entity.setCodec(stream.getCodec());
                    entity.setLanguage(stream.getMetadata().get("language"));
                    entity.setForced(stream.getDispositions().contains(Disposition.FORCED));
                });

                if (subs.containsKey(stream)) {
                    File mediaTrack = subs.get(stream);
                    this.libraryService.storeSubtitle(track, mediaTrack);
                    this.deleteFile(mediaTrack);
                }
            }
            this.episodeService.mod(episode.getId(), entity -> entity.setReady(true));
            LOGGER.info("[{}:{}] The torrent file has been imported.", torrent.getId(), file.getIndex());
        }
    }

    private File detectFile(TorrentFileEntity<?, ?> file) throws IOException {

        return this.libraryService
                .retrieveDownload(file)
                .orElseThrow(() -> new IllegalStateException(
                        String.format(
                                "Could not determine path to file %s of torrent %s",
                                file.getIndex(),
                                file.getTorrent().getId()
                        )));
    }

    private void deleteFile(File file) {

        if (!file.delete()) {
            LOGGER.warn("Could not delete file {}", file.getAbsolutePath());
        }
    }

}
