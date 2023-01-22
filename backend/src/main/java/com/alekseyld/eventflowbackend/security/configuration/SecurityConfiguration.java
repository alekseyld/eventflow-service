package com.alekseyld.eventflowbackend.security.configuration;

import com.alekseyld.eventflowbackend.security.RestAuthenticationEntryPoint;
import com.alekseyld.eventflowbackend.security.configuration.properties.AppSecurityConfigProperties;
import com.alekseyld.eventflowbackend.security.configuration.properties.JwtConfigProperties;
import com.alekseyld.eventflowbackend.security.filter.TokenAuthenticationFilter;
import com.alekseyld.eventflowbackend.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.alekseyld.eventflowbackend.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.alekseyld.eventflowbackend.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.alekseyld.eventflowbackend.security.oauth2.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@RequiredArgsConstructor
@EnableConfigurationProperties({JwtConfigProperties.class, AppSecurityConfigProperties.class})
public class SecurityConfiguration {

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Enable CORS and disable CSRF
        http.cors().and().csrf().disable();

        // Set session management to stateless
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.formLogin().disable().httpBasic().disable();

        http.exceptionHandling().authenticationEntryPoint(new RestAuthenticationEntryPoint());

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
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .redirectionEndpoint()
//                .baseUri("/oauth2/callback/*")
                .baseUri("/login/oauth2/code/*")
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);

        // Add our custom Token based authentication filter
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

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