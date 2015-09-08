package com.github.flaminc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Reddit API credentials
 */
@Component
@ConfigurationProperties(prefix = "reddit-credentials")
@Data
public class RedditCredentials {
    public String secret;
    public String id;
}
