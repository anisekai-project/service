package me.anisekai.toshiko.modules.library.utils;

import java.io.File;
import java.util.*;

public final class FileSystemUtils {

    private FileSystemUtils() {}

    public static List<File> files(File file) {

        List<File> files = Arrays.asList(Objects.requireNonNull(file.listFiles()));
        files.sort(Comparator.comparing(File::getName));
        return files;
    }

    public static List<File> find(File file) {

        List<File> files   = new ArrayList<>();
        List<File> content = files(file);

        content.stream().filter(File::isFile).forEach(files::add);

        content.stream().filter(File::isDirectory)
               .map(FileSystemUtils::find)
               .flatMap(List::stream)
               .forEach(files::add);

        files.sort(Comparator.comparing(File::getAbsolutePath));
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
