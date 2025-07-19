package fr.anisekai.web.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "web")
public class WebConfig {

    private String host;

    public String getHost() {

        return this.host;
    }

    public void setHost(String host) {

        this.host = host;
    }

}
