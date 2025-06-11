package me.anisekai.library.tasks.executors;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.anisekai.wireless.api.json.AnisekaiJson;
import fr.anisekai.wireless.api.media.MediaFile;
import fr.anisekai.wireless.api.media.MediaStream;
import fr.anisekai.wireless.api.media.bin.FFMpeg;
import fr.anisekai.wireless.api.media.enums.Codec;
import me.anisekai.library.LibraryService;
import me.anisekai.server.entities.Episode;
import me.anisekai.server.entities.Torrent;
import me.anisekai.server.entities.TorrentFile;
import me.anisekai.server.entities.Track;
import me.anisekai.server.services.TorrentFileService;
import me.anisekai.server.services.TorrentService;
import me.anisekai.server.services.TrackService;
import me.anisekai.server.tasking.TaskExecutor;
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
    private static final Codec AUDIO_CODEC = Codec.VORBIS;

    public static final String OPTION_TORRENT = "torrent";

    private final LibraryService     libraryService;
    private final TrackService       trackService;
    private final TorrentService     torrentService;
    private final TorrentFileService torrentFileService;

    public MediaImportTask(LibraryService libraryService, TrackService trackService, TorrentService torrentService, TorrentFileService torrentFileService) {

        this.libraryService     = libraryService;
        this.trackService       = trackService;
        this.torrentService     = torrentService;
        this.torrentFileService = torrentFileService;
    }

    @Override
    public boolean validateParams(AnisekaiJson params) {

        return params.has(OPTION_TORRENT);
    }

    @Override
    public void execute(ITimedAction timer, AnisekaiJson params) throws IOException, InterruptedException {

        Torrent           torrent = this.torrentService.fetch(params.getString(OPTION_TORRENT));
        List<TorrentFile> files   = this.torrentFileService.getFiles(torrent);

        for (TorrentFile file : files) {

            File      downloaded = this.detectFile(torrent, file);
            Episode   episode    = file.getEpisode();
            MediaFile media      = MediaFile.of(downloaded);

            LOGGER.info("Converting file {} (Video: {}, Audio: {})", downloaded.getName(), VIDEO_CODEC, AUDIO_CODEC);
            Map<MediaStream, File> tracks = FFMpeg.explode(media, VIDEO_CODEC, AUDIO_CODEC);
            LOGGER.info("The file {} has been converted.", downloaded.getName());

            for (MediaStream stream : tracks.keySet()) {
                File   mediaTrack     = tracks.get(stream);
                String mediaTrackName = mediaTrack.getName().substring(0, mediaTrack.getName().lastIndexOf('.'));

                Codec codec = switch (stream.codec().getType()) {
                    case VIDEO -> VIDEO_CODEC == Codec.VIDEO_COPY ? stream.codec() : VIDEO_CODEC;
                    case AUDIO -> AUDIO_CODEC == Codec.AUDIO_COPY ? stream.codec() : AUDIO_CODEC;
                    default -> stream.codec();
                };

                Track track = this.trackService.getProxy().create(entity -> {
                    entity.setEpisode(episode);
                    entity.setName(mediaTrackName);
                    entity.setCodec(codec);
                    entity.setLanguage(stream.language());
                });

                File destination = this.libraryService.get(track);
                //noinspection ResultOfMethodCallIgnored
                destination.getParentFile().mkdirs();

                Files.move(mediaTrack.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private File detectFile(Torrent torrent, TorrentFile file) {

        File downloaded = this.libraryService.get(file);
        if (downloaded.exists()) return downloaded;
        downloaded = this.libraryService.get(torrent, file);
        if (downloaded.exists()) return downloaded;
        throw new IllegalStateException("Could not determine path to file %s of torrent %s".formatted(
                file.getIndex(),
                torrent.getId()
        ));
    }

}
