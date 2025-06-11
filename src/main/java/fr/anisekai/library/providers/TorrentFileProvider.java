package fr.anisekai.library.providers;

import fr.anisekai.library.interfaces.FileProvider;
import fr.anisekai.server.entities.Torrent;

import java.io.File;

public class TorrentFileProvider implements FileProvider<Torrent> {

    @Override
    public File getFileFrom(Torrent source) {

        return null;
    }

}
