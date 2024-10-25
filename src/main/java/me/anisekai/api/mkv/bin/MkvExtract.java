package me.anisekai.api.mkv.bin;

import me.anisekai.api.mkv.MediaFile;
import me.anisekai.api.mkv.MediaTrack;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class MkvExtract {

    public static String EXECUTOR = "mkvextract";

    private MkvExtract() {}

    private static void exec(String... params) throws Exception {

        exec(Arrays.asList(params));
    }

    private static void exec(List<String> params) throws Exception {

        Runtime      runtime = Runtime.getRuntime();
        List<String> args    = new ArrayList<>();
        args.add(EXECUTOR);
        args.addAll(params);

        Process process = runtime.exec(args.toArray(new String[0]));
        process.waitFor(5, TimeUnit.MINUTES);
    }

    public static Map<MediaTrack, File> extractTracks(MediaFile media, Set<MediaTrack> tracks) throws Exception {

        if (!media.getTracks().containsAll(tracks)) {
            throw new IllegalArgumentException("One or more tracks provided are not part of the media file.");
        }

        List<MediaTrack> nonSupportedTracks = tracks.stream()
                                                    .filter(track -> track.getFormat() == null)
                                                    .toList();

        if (!nonSupportedTracks.isEmpty()) {

            String report = nonSupportedTracks.stream()
                                              .map(track -> String.format(
                                                      "Track %s with codec %s",
                                                      track.getId(),
                                                      track.getCodec()
                                              ))
                                              .collect(Collectors.joining(", "));

            throw new IllegalArgumentException("Unable to extract some tracks (" + report + ")");
        }

        List<String> params = new ArrayList<>();
        params.add(media.getFile().getAbsolutePath());
        params.add("tracks");

        Map<MediaTrack, File> mapping         = new HashMap<>();
        String                mediaName       = media.getFile().getName();
        String                mediaNameNoExt  = mediaName.substring(0, mediaName.lastIndexOf('.'));
        File                  outputDirectory = media.getFile().getParentFile();

        for (MediaTrack track : tracks) {
            // Output file (ex: 08.0.audio.jpn.aac)
            String filename = track.asFileName(mediaNameNoExt);

            File output = new File(outputDirectory, filename);
            mapping.put(track, output);
            params.add(String.format("%s:%s", track.getId(), output.getAbsolutePath()));
        }

        exec(params);
        return mapping;
    }

}
