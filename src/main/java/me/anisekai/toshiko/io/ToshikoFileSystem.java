package me.anisekai.toshiko.io;

import me.anisekai.toshiko.io.video.SubtitleCodec;
import me.anisekai.toshiko.io.video.VideoFile;
import me.anisekai.toshiko.utils.FileSystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Service
public class ToshikoFileSystem {

    private final static List<String> SUPPORTED_EXTENSION = Arrays.asList("mkv", "mp4", "avi");

    private final static Logger LOGGER = LoggerFactory.getLogger(ToshikoFileSystem.class);

    private final DiskService             diskService;
    private final FileManagerService      fileManagerService;
    private final Collection<DiskFile>    diskFileLocking = new HashSet<>();
    private final BlockingDeque<DiskFile> automationQueue = new LinkedBlockingDeque<>();
    private       boolean                 hasFileWaiting  = false;

    public ToshikoFileSystem(DiskService diskService, FileManagerService fileManagerService) {

        this.diskService        = diskService;
        this.fileManagerService = fileManagerService;
    }

    private File getOutput(DiskFile diskFile, String destinationDirectory) {

        String relative  = this.getRelativeFsPath(diskFile.getFile());
        File   animeDir  = new File(destinationDirectory);
        File   output    = new File(animeDir, relative);
        File   container = output.getParentFile();
        container.mkdirs();
        return output;
    }

    private void moveVideo(DiskFile mkv) throws IOException {

        File output = this.getOutput(mkv, this.diskService.getDiskConfiguration().getAnimesOutput());
        Files.move(mkv.getPath(), output.toPath(), StandardCopyOption.ATOMIC_MOVE);
    }

    private void moveSubtitle(DiskFile subtitle) throws IOException {

        File output = this.getOutput(subtitle, this.diskService.getDiskConfiguration().getSubtitlesOutput());
        Files.move(subtitle.getPath(), output.toPath(), StandardCopyOption.ATOMIC_MOVE);
    }

    private String getRelativeFsPath(File file) {

        return file.getAbsolutePath().replace(this.diskService.getAutomationPath().toString(), "");
    }

    public int getAmountInQueue() {

        return this.diskFileLocking.size();
    }

    public int checkForAutomation() {

        if (!this.diskService.isEnabled()) {
            return 0;
        }

        LOGGER.info("Checking automation directory...");

        List<DiskFile> supportedFiles = FileSystemUtils.find(this.diskService.getAutomationPath().toFile())
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

        if (!this.diskService.isEnabled()) {
            return;
        }

        DiskFile diskFile = this.automationQueue.poll();

        if (diskFile == null) {
            return;
        }

        try {
            LOGGER.info("Reading video data...");
            VideoFile videoData = this.fileManagerService.getVideoData(diskFile.getPath());
            LOGGER.info("Extracting subtitles...");
            List<DiskFile> subtitles = this.fileManagerService.extractSubtitles(videoData.getTracks());
            LOGGER.info("Converting subtitles");
            List<DiskFile> webSubtitles = this.fileManagerService.convertSubtitles(subtitles, SubtitleCodec.VTT);
            LOGGER.info("Submitting files to toshiko disk environment...");
            this.moveVideo(diskFile);
            for (DiskFile subtitle : webSubtitles) {
                this.moveSubtitle(subtitle);
            }
            LOGGER.info("Removing leftover files from automation directory...");
            subtitles.forEach(subtitle -> subtitle.getFile().delete());
            this.diskFileLocking.remove(diskFile);
            LOGGER.info("The file '{}' was imported with success.", this.getRelativeFsPath(diskFile.getFile()));

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

}
