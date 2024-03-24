package me.anisekai.api.ffmpeg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class FFMPEG {

    public static String EXECUTOR = "ffmpeg";

    private FFMPEG() {}

    public static void exec(File input, File output) throws Exception {

        Runtime      runtime = Runtime.getRuntime();
        List<String> args    = new ArrayList<>();
        args.add(EXECUTOR);
        args.add("-i");
        args.add(input.getAbsolutePath());
        args.add(output.getAbsolutePath());

        Process process = runtime.exec(args.toArray(new String[0]));
        process.waitFor(1, TimeUnit.HOURS);
    }

}
