package com.alekseyld.eventflowbackend.security.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Getter
@Setter
@ConfigurationProperties("jwt")
public class JwtConfigProperties {

    private RSAPublicKey rsaPublicKey;

    private RSAPrivateKey rsaPrivateKey;
}
