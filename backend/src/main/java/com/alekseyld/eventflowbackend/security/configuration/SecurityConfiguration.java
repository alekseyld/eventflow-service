package com.alekseyld.eventflowbackend.security.configuration;

import com.alekseyld.eventflowbackend.security.configuration.properties.AppSecurityConfigProperties;
import com.alekseyld.eventflowbackend.security.filter.TokenAuthenticationFilter;
import com.alekseyld.eventflowbackend.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.alekseyld.eventflowbackend.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.alekseyld.eventflowbackend.security.oauth2.OAuth2AuthenticationSuccessHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@ConditionalOnProperty(prefix = "app-security", name = "enable-auth")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String DEFAULT_AUTH_URL = "/oauth2/authorize/google";

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final AppSecurityConfigProperties appConfig;

    @NonNull
    private LoginUrlAuthenticationEntryPoint getLoginUrlAuthenticationEntryPoint() {
        var redirect_uri = appConfig.getAuthorizedRedirectUris().get(0);
        var loginFormUrl = String.format("%s?redirect_uri=%s", DEFAULT_AUTH_URL, redirect_uri);

        return new LoginUrlAuthenticationEntryPoint(loginFormUrl);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository,
            TokenAuthenticationFilter tokenAuthenticationFilter
    ) throws Exception {
        // Enable CORS and disable CSRF
        http.cors().and().csrf().disable();

        // Set session management to stateless
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.formLogin().disable().httpBasic().disable();

        http.exceptionHandling().authenticationEntryPoint(getLoginUrlAuthenticationEntryPoint());

        // Set permissions on endpoints
        http.authorizeRequests()
                // Our public endpoints
                .antMatchers("/api/public/**")
                .permitAll()
                .antMatchers("/auth/**", "/oauth2/**", "/login/oauth2/code/**")
                .permitAll()
                // Our private endpoints
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                .and()
                .redirectionEndpoint()
                .baseUri("/login/oauth2/code/*")
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);

        // Add our custom Token based authentication filter
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Used by Spring Security if CORS is enabled.
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}