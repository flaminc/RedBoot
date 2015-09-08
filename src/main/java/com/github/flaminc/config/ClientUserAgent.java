package com.github.flaminc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * User agent configuration for JRAW client
 */
@Component
@ConfigurationProperties(prefix = "client-user-agent")
@Data
public class ClientUserAgent {
    private String platform;
    private String appId;
    private String version;
    private String redditUsername;
}
