package me.anisekai.library.tasks.executors;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.mkv.MediaFile;
import me.anisekai.api.mkv.MediaTrack;
import me.anisekai.api.mkv.bin.MkvExtract;
import me.anisekai.api.mkv.bin.MkvMerge;
import me.anisekai.library.LibraryService;
import me.anisekai.server.entities.Episode;
import me.anisekai.server.entities.Media;
import me.anisekai.server.entities.Torrent;
import me.anisekai.server.services.TorrentService;
import me.anisekai.server.services.TrackService;
import me.anisekai.server.tasking.TaskExecutor;

import java.io.File;
import java.util.Map;

public class MediaImportTask implements TaskExecutor {

    public static final String OPTION_TORRENT = "torrent";

    private final LibraryService libraryService;
    private final TorrentService torrentService;
    private final TrackService   trackService;

    public MediaImportTask(LibraryService libraryService, TorrentService torrentService, TrackService trackService) {

        this.libraryService = libraryService;
        this.torrentService = torrentService;
        this.trackService   = trackService;
    }

    /**
     * Check if the executor can find the required content in the provide {@link BookshelfJson} for its execution.
     *
     * @param params
     *         A {@link BookshelfJson}
     *
     * @return True if the json contains all settings, false otherwise.
     */
    @Override
    public boolean validateParams(BookshelfJson params) {

        return params.has(OPTION_TORRENT);
    }

    /**
     * Run this task.
     *
     * @param timer
     *         The timer to use to mesure performance of the task.
     * @param params
     *         The parameters of this task.
     *
     * @throws Exception
     *         Thew if something happens.
     */
    @Override
    public void execute(ITimedAction timer, BookshelfJson params) throws Exception {

        Torrent torrent = this.torrentService.fetch(params.getString(OPTION_TORRENT));
        Episode episode = torrent.getEpisode();

        File      file  = this.libraryService.getTorrentFile(torrent);
        MediaFile info  = MkvMerge.getInfo(file);
        Media     media = this.libraryService.createMedia(episode);


        Map<MediaTrack, File> trackFileMap = MkvExtract.extractTracks(info, info.getTracks());

        for (MediaTrack track : trackFileMap.keySet()) {


        }
    }

}
