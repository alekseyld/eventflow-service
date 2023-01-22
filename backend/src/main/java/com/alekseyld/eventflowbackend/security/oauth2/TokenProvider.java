package com.alekseyld.eventflowbackend.security.oauth2;

import com.alekseyld.eventflowbackend.security.configuration.properties.AppSecurityConfigProperties;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final AppSecurityConfigProperties appConfig;

    public String createToken(Authentication authentication) {
        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appConfig.getTokenExpirationMsec());

        return Jwts.builder()
                .setSubject(user.getAttribute("email"))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, appConfig.getTokenSecret())
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(appConfig.getTokenSecret())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(appConfig.getTokenSecret()).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

}
