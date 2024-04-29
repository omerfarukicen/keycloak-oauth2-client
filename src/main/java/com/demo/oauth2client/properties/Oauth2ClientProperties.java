package com.demo.oauth2client.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Configuration properties class for OAuth2 client settings.
 * This class holds various OAuth2-related configurations used in the application.
 */

@Validated
@Configuration
@ConfigurationProperties(prefix = "oauth2-client")
@Getter
@Setter
@NoArgsConstructor
public class Oauth2ClientProperties {

    private JwtProperties jwt;
    private String resourceId;
    private String[] permitUrls;
    private CorsProperties cors;
    private String selfToken;

    @Getter
    @Setter
    public static class JwtProperties {
        private String principalAttribute;
        private String publicKey;
        private String algorithm;

    }

    @Getter
    @Setter
    public static class CorsProperties {
        private boolean corsEnabled;
        private List<String> allowedOrigins;
    }
}