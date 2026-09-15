package com.xe.ratealerts.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "xecd")
@Configuration
@Data
public class AppConfig {
    private String accountId;
    private String apiKey;
    private String url;
}
