package me.anisekai.discord.responses.embeds;

import fr.anisekai.wireless.api.plannifier.data.CalibrationResult;
import fr.anisekai.wireless.remote.interfaces.BroadcastEntity;
import fr.anisekai.wireless.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;

public class CalibrationEmbed extends EmbedBuilder {

    public void setCalibrationResult(BroadcastEntity<?> broadcast, CalibrationResult result) {

        this.addField("Broadcast ID", broadcast.getId().toString(), false);
        //noinspection DataFlowIssue
        this.addField("Broadcast Discord ID", broadcast.getEventId().toString(), false);
        this.addField("Anime", broadcast.getWatchTarget().getTitle(), false);
        this.addField("Content", StringUtils.getPlanifiableDescription(broadcast), false);
        this.addBlankField(false);
        this.addField("Updated", String.valueOf(result.updateCount()), false);
        this.addField("Deleted", String.valueOf(result.deleteCount()), false);
    }

}
