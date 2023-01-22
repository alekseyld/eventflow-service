package com.alekseyld.eventflowbackend.security.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@ConditionalOnProperty(prefix = "app-security", name = "disable-auth")
@Configuration
@EnableWebSecurity
public class DebugSecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .anyRequest()
                .permitAll()
                .and()
                .build();
    }
}
