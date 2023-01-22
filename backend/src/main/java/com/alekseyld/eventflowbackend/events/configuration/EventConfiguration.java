package com.alekseyld.eventflowbackend.events.configuration;

import com.alekseyld.eventflowbackend.events.configuration.properties.RedashConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RedashConfigurationProperties.class)
public class EventConfiguration {
}
