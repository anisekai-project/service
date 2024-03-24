package me.anisekai.api.mkv.bin;

import me.anisekai.api.json.BookshelfJson;
import me.anisekai.api.mkv.MediaFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class MkvMerge {

    public static String EXECUTOR = "mkvmerge";

    private MkvMerge() {}

    private static String exec(String... params) throws Exception {

        Runtime      runtime = Runtime.getRuntime();
        List<String> args    = new ArrayList<>();
        args.add(EXECUTOR);
        args.addAll(Arrays.asList(params));

        Process process = runtime.exec(args.toArray(new String[0]));

        return new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    public static MediaFile getInfo(File file) throws Exception {

        String output = exec("-J", file.getAbsolutePath());
        return new MediaFile(file, new BookshelfJson(output));
    }

}
