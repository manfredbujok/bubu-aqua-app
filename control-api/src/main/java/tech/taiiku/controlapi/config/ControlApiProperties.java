package tech.taiiku.controlapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tech-taiiku.control-api")
public record ControlApiProperties(
        String accessClientId,
        String accessClientSecret,
        String seleniumStatusUrl
) {
}
