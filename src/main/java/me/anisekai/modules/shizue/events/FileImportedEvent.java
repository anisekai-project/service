package me.anisekai.modules.shizue.events;

import me.anisekai.globals.events.LoggableEvent;
import me.anisekai.modules.freya.entities.detached.disk.DiskFile;
import me.anisekai.globals.utils.Embedding;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.context.ApplicationEvent;

import java.awt.*;

public class FileImportedEvent extends ApplicationEvent implements LoggableEvent {

    private final DiskFile file;

    public FileImportedEvent(Object source, DiskFile file) {

        super(source);
        this.file = file;
    }


    @Override
    public EmbedBuilder asEmbed() {

        return Embedding.event(this)
                        .setTitle("Un import a été terminé")
                        .setDescription(String.format(
                                "L'épisode %s a été importé.\nFichier: `%s`",
                                this.file.getName(),
                                this.file.getPath()
                        ))
                        .setColor(Color.GREEN);
    }

}
