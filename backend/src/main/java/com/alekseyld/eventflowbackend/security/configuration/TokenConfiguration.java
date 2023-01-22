package com.alekseyld.eventflowbackend.security.configuration;

import com.alekseyld.eventflowbackend.security.configuration.properties.AppSecurityConfigProperties;
import com.alekseyld.eventflowbackend.security.filter.TokenAuthenticationFilter;
import com.alekseyld.eventflowbackend.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.alekseyld.eventflowbackend.security.oauth2.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AppSecurityConfigProperties.class)
public class TokenConfiguration {

    private final TokenProvider tokenProvider;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    /**
     * By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
     * the authorization request. But, since our service is stateless, we can't save it in
     * the session. We'll save the request in a Base64 encoded cookie instead.
     */
    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }
}
