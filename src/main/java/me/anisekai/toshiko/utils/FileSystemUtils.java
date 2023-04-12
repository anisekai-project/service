package me.anisekai.toshiko.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class FileSystemUtils {

    private FileSystemUtils() {}

    public static List<File> files(File file) {

        List<File> files = Arrays.asList(Objects.requireNonNull(file.listFiles()));
        files.sort(Comparator.comparing(File::getName));
        return files;
    }

    public static List<File> files(String path) {

        File file = new File(path);

        if (!file.exists() && !file.isDirectory()) {
            throw new IllegalStateException("Could not list content at: " + path);
        }

        return files(file);
    }
}
