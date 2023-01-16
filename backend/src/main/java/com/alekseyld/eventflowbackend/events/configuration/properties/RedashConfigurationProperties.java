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

    @NonNull
    private String sessionCookie;

    @NonNull
    private String eventFetchUrl;

    @NonNull
    private String jobGetUrl;

    @NonNull
    private String eventSqlQuery;

    @NonNull
    private String eventSqlTable;
}
