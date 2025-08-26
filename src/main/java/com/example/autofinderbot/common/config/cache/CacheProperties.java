package com.example.autofinderbot.common.config.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("cacheProps")
@ConfigurationProperties(prefix = "cache")
@Getter @Setter
public class CacheProperties {
    private String carUrl = "car_urls";
    private int maxSize = 100;
}
