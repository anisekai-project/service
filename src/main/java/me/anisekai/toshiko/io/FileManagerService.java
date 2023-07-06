package me.anisekai.toshiko.io;

import me.anisekai.toshiko.io.video.MediaTrack;
import me.anisekai.toshiko.io.video.SubtitleCodec;
import me.anisekai.toshiko.io.video.VideoFile;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileManagerService.class);

    public VideoFile getVideoData(Path path) throws IOException {

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(new String[]{
                "mkvmerge",
                "-J",
                path.toFile().getAbsolutePath()
        });

        InputStream       is  = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader    br  = new BufferedReader(isr);

        StringBuilder sb = new StringBuilder();
        String        s;

        //noinspection NestedAssignment
        while ((s = br.readLine()) != null) {
            sb.append(s);
        }

        JSONObject obj = new JSONObject(sb.toString());
        return new VideoFile(path.toFile(), obj);
    }

    public DiskFile extractSubtitle(MediaTrack track) throws Exception {

        DiskFile video = new DiskFile(track.getVideoFile().getFile().toPath());
        DiskFile extracted = new DiskFile(video, String.format(
                "%s-%s",
                video.getName(),
                track.getId()
        ),
                                          track.getCodec().getExtension()
        );

        LOGGER.info(
                "mkvmerge: Extracting TrackID {} of file '{}' into '{}'...",
                track.getId(),
                video.getFilename(),
                extracted.getFilename()
        );

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(new String[]{
                "mkvextract",
                "tracks",
                video.getFile().getAbsolutePath(),
                String.format("%s:%s", track.getId(), extracted.getFile().getAbsolutePath())
        });

        int exitCode = process.waitFor();

        // 0 = OK | 1 = OK with Warnings ?
        if (exitCode != 0 && exitCode != 1) {
            LOGGER.error(
                    "mkvmerge: TrackID {} of file '{}' could not be extracted (exit code {})",
                    track.getId(),
                    video.getFilename(),
                    exitCode
            );
            throw new IllegalStateException("mkvmerge exit code is " + exitCode);
        }
        LOGGER.info("mkvmerge: TrackID {} of file '{}' has been extracted.", track.getId(), video.getFilename());
        // Fix subtitle file by removing comments which make ffmpeg fail to convert the file later.
        LOGGER.info("Fixing subtitle '{}' file...", extracted.getFilename());
        List<String> lines = Files.readAllLines(extracted.getFile().toPath())
                                  .stream()
                                  .filter(line -> !line.startsWith(";"))
                                  .filter(line -> !line.startsWith("\uFEFF;"))
                                  .toList();

        if (!extracted.getFile().delete()) {
            LOGGER.warn("Unable to delete the current subtitle file. The file might be corrupted.");
        }

        Files.write(
                extracted.getFile().toPath(),
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
        );
        LOGGER.info("Subtitle file fixed.");

        return extracted;
    }

    public DiskFile convertSubtitle(DiskFile source, SubtitleCodec codec) throws Exception {

        DiskFile target = new DiskFile(source, source.getName(), codec.getExtension());
        LOGGER.info("ffmpeg: Converting '{}' to '{}'...", source.getFilename(), target.getFilename());

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(new String[]{
                "ffmpeg",
                "-i",
                source.getFile().getAbsolutePath(),
                target.getFile().getAbsolutePath()
        });

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            LOGGER.error("ffmpeg: File '{}' could not be converted (exit code {})", source.getFilename(), exitCode);
            throw new IllegalStateException("ffmpeg exit code is " + exitCode);
        }

        return target;
    }

    public List<DiskFile> extractSubtitles(Iterable<MediaTrack> tracks) throws Exception {

        List<DiskFile> result = new ArrayList<>();
        for (MediaTrack track : tracks) {
            result.add(this.extractSubtitle(track));
        }
        return result;
    }

    public List<DiskFile> convertSubtitles(Iterable<DiskFile> files, SubtitleCodec codec) throws Exception {

        List<DiskFile> result = new ArrayList<>();
        for (DiskFile file : files) {
            result.add(this.convertSubtitle(file, codec));
        }
        return result;
    }

}
