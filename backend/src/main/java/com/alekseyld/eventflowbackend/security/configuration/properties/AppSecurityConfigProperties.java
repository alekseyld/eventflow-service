package com.alekseyld.eventflowbackend.security.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties("app-security")
public class AppSecurityConfigProperties {

    private List<String> authorizedRedirectUris = new ArrayList<>();
    private long tokenExpirationMsec;

    private String tokenSecret;

    private boolean enableAuth;
    private boolean disableAuth;
}
