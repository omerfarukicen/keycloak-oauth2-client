package com.demo.oauth2client.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.demo.oauth2client.config.handler.CustomAccessDeniedHandler;
import com.demo.oauth2client.config.handler.CustomAuthenticationEntryPoint;
import com.demo.oauth2client.properties.Oauth2ClientProperties;

import java.util.List;

/**
 * Configuration class responsible for defining security settings for the OAuth2 resource.
 * This class configures security filters, JWT decoding, authorization rules, and CORS (Cross-Origin Resource Sharing).
 */
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@Slf4j
@PropertySources({
        @PropertySource("oauth2-client.properties")
})

public class ResourceSecurityConfig {
    private final JwtSecurityConverter jwtSecurityConverter;
    private final Oauth2ClientProperties oauth2ClientProperties;
    private final Environment environment;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    @PostConstruct
    public void info() {
        if (oauth2ClientProperties.getSelfToken() != null && !oauth2ClientProperties.getSelfToken().isEmpty()) {
            log.info("SELF Token İşlendi. JWT: " + oauth2ClientProperties.getSelfToken());
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(oauth2ClientProperties.getPermitUrls()).permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler))
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtSecurityConverter)).authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(csrf -> csrf.disable())
                // CORS yapılandırması
                .cors(cors -> {
                    if (oauth2ClientProperties.getCors().isCorsEnabled()) {
                        cors.configurationSource(this.corsConfigurationSource());
                    } else {
                        cors.disable();
                    }
                    // CorsConfig gereken ayarlamalar yapılmıştır.
                }).build();
    }

    @Bean
    @ConditionalOnProperty(name = "oauth2-client.cors.corsEnabled", havingValue = "true", matchIfMissing = false)
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("Cors ayarlamalarınız yapılmıştır.Origins: " + oauth2ClientProperties.getCors().getAllowedOrigins());
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedMethods(List.of("POST", "GET", "PUT", "PATCH", "DELETE"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowedOrigins(oauth2ClientProperties.getCors().getAllowedOrigins());
        corsConfig.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

}