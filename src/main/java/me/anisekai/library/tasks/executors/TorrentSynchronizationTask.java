package me.anisekai.library.tasks.executors;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.transmission.TorrentStatus;
import me.anisekai.api.transmission.TransmissionTorrent;
import me.anisekai.library.services.SpringTransmissionClient;
import me.anisekai.server.entities.Torrent;
import me.anisekai.server.services.TorrentService;
import me.anisekai.server.tasking.TaskExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TorrentSynchronizationTask implements TaskExecutor {

    private final TorrentService torrentService;

    public TorrentSynchronizationTask(TorrentService torrentService) {

        this.torrentService = torrentService;
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

        timer.action("client-check", "Check the transmission client");
        SpringTransmissionClient client = this.torrentService.getClient();
        client.check();
        if (!client.isAvailable()) throw new IllegalArgumentException("Transmission client is not available");
        timer.endAction();

        timer.action("load-torrents", "Load torrents");
        timer.action("remote", "Load remote torrents");
        Set<TransmissionTorrent> torrents = this.torrentService.getClient().getTorrents();
        timer.endAction();

        timer.action("local", "Load local torrents");
        List<Torrent> downloading = this.torrentService.getAllDownloading();
        timer.endAction();
        timer.endAction();

        timer.action("sync-torrents", "Load remote torrents");
        for (Torrent downloadingTorrent : downloading) {
            timer.action("sync-torrent", "Sync torrent " + downloadingTorrent.getId());

            // Find a matching torrent in the download list.
            Optional<TransmissionTorrent> optionalTransmissionTorrent = torrents
                    .stream()
                    .filter(item -> item.getHash().equals(downloadingTorrent.getId()))
                    .findFirst();


            if (optionalTransmissionTorrent.isPresent()) {
                TransmissionTorrent transmissionTorrent = optionalTransmissionTorrent.get();
                this.torrentService.mod(
                        downloadingTorrent.getId(),
                        entity -> {
                            entity.setProgress(transmissionTorrent.getPercentDone());
                            entity.setStatus(transmissionTorrent.getStatus());
                        }
                );

                if (transmissionTorrent.getStatus().isFinished()) {
                    timer.action("submit-task", "Submit media importation task");
                    // TODO: Queue media import
                    timer.endAction();
                }
            } else {
                this.torrentService.mod(downloadingTorrent.getId(), entity -> entity.setStatus(TorrentStatus.UNKNOWN));
            }

            timer.endAction();
        }
        timer.endAction();
    }

}
