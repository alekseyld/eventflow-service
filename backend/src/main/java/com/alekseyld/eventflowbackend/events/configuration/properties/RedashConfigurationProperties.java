package com.alekseyld.eventflowbackend.events.configuration.properties;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("redash")
public class RedashConfigurationProperties {

    @NonNull
    private String basicAuthToken;
}
