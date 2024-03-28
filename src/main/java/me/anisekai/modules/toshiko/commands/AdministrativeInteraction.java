package me.anisekai.modules.toshiko.commands;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import me.anisekai.modules.chiya.entities.DiscordUser;
import me.anisekai.modules.freya.entities.detached.disk.DiskAnime;
import me.anisekai.modules.freya.entities.detached.disk.DiskGroup;
import me.anisekai.modules.freya.services.DiskService;
import me.anisekai.modules.freya.services.ToshikoFileSystem;
import me.anisekai.modules.linn.entities.Anime;
import me.anisekai.modules.linn.services.data.AnimeDataService;
import me.anisekai.modules.toshiko.annotations.InteractionBean;
import me.anisekai.modules.toshiko.messages.responses.SimpleResponse;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@InteractionBean
@Component
public class AdministrativeInteraction {

    private final AnimeDataService  service;
    private final ToshikoFileSystem fsService;

    public AdministrativeInteraction(AnimeDataService service, ToshikoFileSystem fsService) {

        this.service   = service;
        this.fsService = fsService;
    }

    @Interact(
            name = "disk/import",
            description = "\uD83D\uDD12 Import file from the automation folder"
    )
    public SlashResponse importCache(DiscordUser user) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Va t'faire foutre", false, true);
        }

        int count = this.fsService.checkForAutomation();
        return new SimpleResponse(String.format("%s anime vont être importés.", count), false, true);
    }

    @Interact(
            name = "disk/link",
            description = "\uD83D\uDD12 Link an anime to a disk folder",
            options = {
                    @Option(
                            name = "anime",
                            description = "The anime to link",
                            type = OptionType.INTEGER,
                            autoComplete = true,
                            required = true
                    ),
                    @Option(
                            name = "disk",
                            description = "The directory",
                            type = OptionType.STRING,
                            autoComplete = true,
                            autoCompleteName = "diskAnime",
                            required = true
                    )
            }
    )
    public SlashResponse diskLink(DiscordUser user, @Param("anime") long animeId, @Param("disk") String disk) {

        if (!user.isAdmin()) {
            return new SimpleResponse("Va t'faire foutre", false, true);
        }

        DiskService diskService = this.fsService.getDiskService();
        DiskAnime   diskAnime   = null;
        DiskGroup   diskGroup   = null;

        for (DiskAnime iDiskAnime : diskService.getDatabase()) {
            for (DiskGroup iDiskGroup : iDiskAnime.getGroups()) {
                if (iDiskGroup.getUuid().toString().equals(disk)) {
                    diskGroup = iDiskGroup;
                    diskAnime = iDiskAnime;
                    break;
                }
            }
        }

        if (diskGroup == null) {
            return new SimpleResponse("Impossible de faire cette association", false, true);
        }

        String diskPath = String.format("%s/%s", diskAnime.getFile().getName(), diskGroup.getFile().getName());

        Anime anime = this.service.mod(animeId, item -> item.setDiskPath(diskPath));
        return new SimpleResponse(String.format(
                "L'anime **%s** a été associé au chemin `%s`",
                anime.getName(),
                diskPath
        ), false, true);
    }

}
