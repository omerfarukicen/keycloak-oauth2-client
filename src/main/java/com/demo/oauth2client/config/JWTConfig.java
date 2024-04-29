package com.demo.oauth2client.config;


import com.demo.oauth2client.properties.Oauth2ClientProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class JWTConfig {
    private final Oauth2ClientProperties oauth2ClientProperties;

    @Bean
    public JwtDecoder jwtDecoderByPublicKeyValue() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(getPublicKeyFromFile())
                .signatureAlgorithm(SignatureAlgorithm.from(oauth2ClientProperties.getJwt().getAlgorithm()))
                .build();
        jwtDecoder.setJwtValidator(getValidators(JwtValidators::createDefault));
        return jwtDecoder;
    }

    private OAuth2TokenValidator<Jwt> getValidators(Supplier<OAuth2TokenValidator<Jwt>> defaultValidator) {
        return defaultValidator.get();
    }

    @SneakyThrows
    private RSAPublicKey getPublicKeyFromFile() {
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(getKeySpec(String.valueOf(oauth2ClientProperties.getJwt().getPublicKey()))));
    }

    private byte[] getKeySpec(String keyValue) {
        keyValue = keyValue.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
        return Base64.getMimeDecoder().decode(keyValue);
    }
}
