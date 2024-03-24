package me.anisekai.modules.freya.services;

import me.anisekai.api.ffmpeg.FFMPEG;
import me.anisekai.api.mkv.MediaFile;
import me.anisekai.api.mkv.MediaFormat;
import me.anisekai.api.mkv.MediaTrack;
import me.anisekai.api.mkv.MediaTrackType;
import me.anisekai.api.mkv.bin.MkvExtract;
import me.anisekai.api.mkv.bin.MkvMerge;
import me.anisekai.modules.freya.entities.detached.disk.DiskFile;
import me.anisekai.modules.freya.events.torrent.TorrentStatusUpdatedEvent;
import me.anisekai.modules.freya.utils.AnimeRenamer;
import me.anisekai.modules.freya.utils.FileSystemUtils;
import me.anisekai.modules.shizue.events.FileImportedEvent;
import me.anisekai.modules.shizue.events.ImportStartedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class ToshikoFileSystem {

    private final static List<String> SUPPORTED_EXTENSION = Arrays.asList("mkv", "mp4", "avi");

    private final static Logger LOGGER = LoggerFactory.getLogger(ToshikoFileSystem.class);

    private final ApplicationEventPublisher publisher;
    private final DiskService               diskService;
    private final Collection<DiskFile>      diskFileLocking = new HashSet<>();
    private final BlockingDeque<DiskFile>   automationQueue = new LinkedBlockingDeque<>();
    private       boolean                   hasFileWaiting  = false;

    public ToshikoFileSystem(ApplicationEventPublisher publisher, DiskService diskService) {

        this.publisher   = publisher;
        this.diskService = diskService;
    }

    private File getOutput(DiskFile diskFile, File target) {

        String relative  = this.getRelativeFsPath(diskFile.getFile());
        File   output    = new File(target, relative);
        File   container = output.getParentFile();
        //noinspection ResultOfMethodCallIgnored
        container.mkdirs();
        return output;
    }

    private void moveVideo(DiskFile mkv) throws IOException {

        File output = this.getOutput(mkv, this.diskService.getAnimesRoot());
        Files.move(mkv.getPath(), output.toPath(), StandardCopyOption.ATOMIC_MOVE);
    }

    private void moveSubtitle(DiskFile subtitle) throws IOException {

        File output = this.getOutput(subtitle, this.diskService.getSubtitlesRoot());
        Files.move(subtitle.getPath(), output.toPath(), StandardCopyOption.ATOMIC_MOVE);
    }

    private String getRelativeFsPath(File file) {

        return file.getAbsolutePath().replace(this.diskService.getAutomationDirectory().getAbsolutePath(), "");
    }

    public int getAmountInQueue() {

        return this.diskFileLocking.size();
    }

    public int checkForAutomation() {

        if (!this.diskService.getConfiguration().isAutoDownloadEnabled()) {
            return 0;
        }

        LOGGER.info("Checking automation directory...");

        List<DiskFile> supportedFiles = FileSystemUtils.find(this.diskService.getAutomationDirectory())
                                                       .stream()
                                                       .map(DiskFile::new)
                                                       .filter(diskFile -> SUPPORTED_EXTENSION.contains(diskFile.getExtension()))
                                                       .filter(diskFile -> !this.diskFileLocking.contains(diskFile))
                                                       .toList();

        List<DiskFile> manageableFiles = supportedFiles.stream().filter(DiskFile::isReady).toList();
        this.hasFileWaiting = supportedFiles.size() > manageableFiles.size();

        LOGGER.info("Queuing {} file(s) in automation directory.", supportedFiles.size());

        supportedFiles.forEach(diskFile -> {
            LOGGER.debug(" > Queuing '{}'...", this.getRelativeFsPath(diskFile.getFile()));
            this.diskFileLocking.add(diskFile);
            this.automationQueue.add(diskFile);
        });

        return supportedFiles.size();
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void handleNextFile() {

        if (!this.diskService.getConfiguration().isAutoDownloadEnabled()) {
            return;
        }

        DiskFile diskFile = this.automationQueue.poll();

        if (diskFile == null) {
            return;
        }

        this.publisher.publishEvent(new ImportStartedEvent(this, diskFile));

        try {
            LOGGER.info("Reading video data...");
            MediaFile media          = MkvMerge.getInfo(diskFile.getFile());
            String    mediaName      = media.getFile().getName();
            String    mediaNameNoExt = mediaName.substring(0, mediaName.lastIndexOf('.'));

            LOGGER.info("Extracting subtitles...");
            Set<MediaTrack>       subsTracks  = media.getTracks(MediaTrackType.SUBTITLES);
            Map<MediaTrack, File> subsMapping = MkvExtract.extractTracks(media, subsTracks);

            LOGGER.info("Converting subtitles");
            Collection<DiskFile> webSubtitles = new ArrayList<>();
            for (MediaTrack subsTrack : subsMapping.keySet()) {
                File   subsFile = subsMapping.get(subsTrack);
                String name     = subsTrack.asFileName(mediaNameNoExt, MediaFormat.WEB_VTT);
                File   output   = new File(subsFile.getParentFile(), name);
                FFMPEG.exec(subsFile, output);
                webSubtitles.add(new DiskFile(output));
            }

            LOGGER.info("Submitting files to toshiko disk environment...");
            this.moveVideo(diskFile);
            for (DiskFile subtitle : webSubtitles) {
                this.moveSubtitle(subtitle);
            }
            LOGGER.info("Removing leftover files from automation directory...");
            //noinspection ResultOfMethodCallIgnored
            subsMapping.values().forEach(File::delete);
            this.diskFileLocking.remove(diskFile);

            LOGGER.info("The file '{}' was imported with success.", this.getRelativeFsPath(diskFile.getFile()));
            this.publisher.publishEvent(new FileImportedEvent(this, diskFile));

            if (!this.hasFileWaiting && this.automationQueue.isEmpty()) {
                LOGGER.info("Automation folder is empty, rebuilding cache...");
                this.diskService.cache();
                LOGGER.info("Cache refreshed.");
            } else if (this.getAmountInQueue() % 10 == 0) {
                this.diskService.cache();
                LOGGER.info("Cache refreshed.");
            }
        } catch (Exception e) {
            // Put it back in the queue (maybe it's a temporary failure ?)
            this.automationQueue.offer(diskFile);
            LOGGER.error("Failed to handle file {}: {}", this.getRelativeFsPath(diskFile.getFile()), e.getMessage());
            LOGGER.error("   > Failure reason:", e);
        }
    }

    @EventListener
    public void onTorrentStatusUpdated(TorrentStatusUpdatedEvent event) throws IOException {

        if (!event.getCurrent().isFinished()) {
            return;
        }

        // Try to find the file.
        File       torrentDirectory = this.diskService.getTorrentsDirectory();
        List<File> content          = FileSystemUtils.files(torrentDirectory);

        Optional<File> optionalFile = content.stream()
                                             .filter(file -> file.getName().equals(event.getEntity().getFile()))
                                             .findFirst();

        if (optionalFile.isEmpty()) {
            LOGGER.warn("Could not finalize {}", event.getEntity().getName());
            return;
        }

        File episode     = optionalFile.get();
        File automation  = this.diskService.getAutomationDirectory();
        File destination = new File(automation, event.getEntity().getAnime().getDiskPath());
        if (!destination.mkdirs()) {
            LOGGER.warn(
                    "Unable to create destination folder for anime {}. Further file management could lead to errors.",
                    event.getEntity().getAnime().getName()
            );
        }

        AnimeRenamer.rename(episode, destination);
        this.checkForAutomation();
    }

}
