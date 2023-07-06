package me.anisekai.toshiko.io;

import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class DiskFile {

    private final File   file;
    private final Path   path;
    private final String filename;
    private final String name;
    private final String extension;

    public DiskFile(File file) {

        this(file.toPath());
    }

    public DiskFile(Path path) {

        this.file      = path.toFile();
        this.path      = path;
        this.filename  = this.file.getName();
        this.name      = this.filename.substring(0, this.filename.lastIndexOf('.'));
        this.extension = this.filename.substring(this.filename.lastIndexOf('.') + 1);
    }

    public DiskFile(DiskFile other, String name, String extension) {

        this.name      = name;
        this.extension = extension;
        this.filename  = String.format("%s.%s", this.name, this.extension);
        this.file      = new File(other.getFile().getParentFile(), this.filename);
        this.path      = this.file.toPath();
    }

    public File getFile() {

        return this.file;
    }

    public Path getPath() {

        return this.path;
    }

    public String getFilename() {

        return this.filename;
    }

    public String getName() {

        return this.name;
    }

    public String getExtension() {

        return this.extension;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        DiskFile diskFile = (DiskFile) o;
        return Objects.equals(this.path, diskFile.path);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.path);
    }

    @Override
    public String toString() {

        return String.format(
                "DiskFile{filename='%s', name='%s', extension='%s'}",
                this.filename,
                this.name,
                this.extension
        );
    }

    public boolean isReady() {

        if (!this.file.canWrite()) {
            return false;
        }

        try (FileChannel channel = FileChannel.open(this.path, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            try (FileLock ignored = channel.lock()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
