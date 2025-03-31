package me.anisekai.discord.responses.embeds;

import me.anisekai.api.plannifier.data.CalibrationResult;
import me.anisekai.server.interfaces.IBroadcast;
import me.anisekai.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;

public class CalibrationEmbed extends EmbedBuilder {

    public void setCalibrationResult(IBroadcast<?> broadcast, CalibrationResult result) {

        this.addField("Broadcast ID", broadcast.getId().toString(), false);
        this.addField("Broadcast Discord ID", broadcast.getEventId().toString(), false);
        this.addField("Anime", broadcast.getWatchTarget().getTitle(), false);
        this.addField("Content", StringUtils.getPlannifiableDescription(broadcast), false);
        this.addBlankField(false);
        this.addField("Updated", String.valueOf(result.getUpdateCount()), false);
        this.addField("Deleted", String.valueOf(result.getDeleteCount()), false);

    }

}
