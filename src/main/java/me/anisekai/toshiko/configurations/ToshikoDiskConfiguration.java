package me.anisekai.toshiko.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "toshiko.disk")
public class ToshikoDiskConfiguration {

    /**
     * Absolute path pointing to the directory where imported anime will be copied to.
     */
    private String animesOutput;
    /**
     * Absolute path pointing to the directory where imported anime's subtitles will be copied to.
     */
    private String subtitlesOutput;
    /**
     * Absolute path pointing to the directory where anime to import are located
     */
    private String animesInput;


    public String getAnimesOutput() {

        return this.animesOutput;
    }

    public void setAnimesOutput(String animesOutput) {

        this.animesOutput = animesOutput;
    }

    public String getSubtitlesOutput() {

        return this.subtitlesOutput;
    }

    public void setSubtitlesOutput(String subtitlesOutput) {

        this.subtitlesOutput = subtitlesOutput;
    }

    public String getAnimesInput() {

        return this.animesInput;
    }

    public void setAnimesInput(String animesInput) {

        this.animesInput = animesInput;
    }
}
