package me.anisekai.toshiko.modules.library.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AnimeRenamer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnimeRenamer.class);

    public static final Map<String, Pattern> REGEX_STORE = new TreeMap<>() {{
        this.put("1-SEASON", Pattern.compile("(\\[(?<team>.*)] )?.* S\\d{2}?E(?<ep>\\d{2}).*\\.(?<ext>.*)"));
        this.put("2-SEASON-SPACE", Pattern.compile("(\\[(?<team>.*)] )?.* S\\d{1,2} E(?<ep>\\d{2}).*\\.(?<ext>.*)"));
        this.put("3-SEASON-DOTTED", Pattern.compile("(.*\\.)*S\\d{2}E(?<ep>\\d{2})(\\..*)*\\.(?<ext>.*)"));
        this.put("4-GLOBAL", Pattern.compile("(\\[(?<team>.*)] )?.* - (?<ep>\\d{2}).*\\.(?<ext>.*)"));
        this.put("5-DOTTED", Pattern.compile("(.*\\.)*E(?<ep>\\d{2})(\\..*)*\\.(?<ext>.*)"));
        this.put("6-NO-META", Pattern.compile(".* (?<ep>\\d{2}) - .*\\.(?<ext>.*)"));
        this.put("7-GLOBAL-NODASH", Pattern.compile("(\\[(?<team>.*)] )?.* (?<ep>\\d{2}) .*\\.(?<ext>.*)"));
        this.put("8-SPECIAL", Pattern.compile("(\\[(?<team>.*)] )?.* SP(?<ep>\\d{2}).*\\.(?<ext>.*)"));
        this.put("9-SIMPLE", Pattern.compile(".*(?<ep>\\d{2}).*\\.(?<ext>.*)"));
    }};

    private AnimeRenamer() {

    }

    public static void rename(File source, File destination) throws IOException {

        for (Map.Entry<String, Pattern> entry : REGEX_STORE.entrySet()) {
            String  regexName = entry.getKey();
            Pattern pattern   = entry.getValue();
            Matcher matcher   = pattern.matcher(source.getName());

            if (matcher.matches()) {
                String renameAs = "%02d.%s";
                int    ep       = Integer.parseInt(matcher.group("ep"));
                String ext      = matcher.group("ext");

                String newName = String.format(renameAs, ep, ext);
                File   target  = new File(destination, newName);

                LOGGER.info("Renaming {} to {} using regex {}", source.getName(), newName, regexName);
                Files.move(source.toPath(), target.toPath());
                return;
            }
        }
        LOGGER.error("Unable to rename {}: No regex matched the filename.", source.getName());
    }

}
