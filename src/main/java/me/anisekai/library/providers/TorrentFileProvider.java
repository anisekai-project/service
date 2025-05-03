package me.anisekai.library.providers;

import me.anisekai.library.interfaces.FileProvider;
import me.anisekai.server.entities.Torrent;

import java.io.File;

public class TorrentFileProvider implements FileProvider<Torrent> {

    @Override
    public File getFileFrom(Torrent source) {

        return null;
    }

}
