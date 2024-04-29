package com.demo.oauth2client.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import com.demo.oauth2client.properties.Oauth2ClientProperties;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A converter class responsible for converting a JSON Web Token (JWT) into an AbstractAuthenticationToken.
 * This converter is used in the Spring Security configuration to customize JWT authentication handling.
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtSecurityConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final Oauth2ClientProperties properties;

    /**
     * Converts a JWT into an AbstractAuthenticationToken with JWT authentication.
     *
     * @param jwt The JSON Web Token to be converted.
     * @return An AbstractAuthenticationToken containing JWT authentication information.
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        return new JwtAuthenticationToken(jwt, getResourceRoles(jwt), getPrincipal(jwt));
    }

    /**
     * Retrieves the principal attribute from the JWT.
     *
     * @param jwt The JSON Web Token.
     * @return The principal attribute value.
     */
    private String getPrincipal(Jwt jwt) {
        String principalAttribute = properties.getJwt().getPrincipalAttribute();
        return jwt.getClaim(principalAttribute);
    }

    /**
     * Retrieves the resource roles from the JWT claim.
     *
     * @param jwt The JSON Web Token.
     * @return A collection of GrantedAuthority representing resource roles.
     * @throws AuthorizationServiceException If the resource roles are not found in the JWT claim.
     */
    @SneakyThrows
    private Collection<? extends GrantedAuthority> getResourceRoles(Jwt jwt) {

        if (!jwt.hasClaim("resource_access")) {
            log.error("Kullanıcının uygulamalara erişimi bulunmamaktadır! JWT içerisinde resource access bulunmamaktadır!");
            throw new AccessDeniedException("Kullanıcının uygulamalara erişimi bulunmamaktadır!");
        }
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (!resourceAccess.containsKey(properties.getResourceId())) {
            log.error(properties.getResourceId() + "  erişiminiz bulunmamaktadır!");
            throw new AccessDeniedException(properties.getResourceId() + "  erişiminiz bulunmamaktadır!");
        }
        Map<String, Object> resourceClient = (Map<String, Object>) resourceAccess.get(properties.getResourceId());
        List<String> clientPermissions = (List<String>) resourceClient.get("roles");
        return clientPermissions.parallelStream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
