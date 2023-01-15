package com.alekseyld.eventflowbackend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
public class EventflowSecurityConfiguration {

    @Bean
    public SecurityFilterChain oauth2SecurityWebFilterChain(
            HttpSecurity httpSecurity
    ) throws Exception {
        httpSecurity
                .anonymous().disable()
                .cors().disable()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable();

        return httpSecurity.build();
    }
}
