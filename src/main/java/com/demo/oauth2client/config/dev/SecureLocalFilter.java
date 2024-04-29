package com.demo.oauth2client.config.dev;

import com.demo.oauth2client.properties.Oauth2ClientProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Custom filter class responsible for extracting user information from the JWT and initializing the session.
 * This filter is executed once per incoming request to populate session information based on the JWT claims.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Profile({"dev", "local", "default"})
public class SecureLocalFilter extends OncePerRequestFilter {

    private final Oauth2ClientProperties oauth2ClientProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (oauth2ClientProperties.getSelfToken() != null && !oauth2ClientProperties.getSelfToken().isEmpty()) {
            String uri = request.getRequestURI();
            if (Arrays.stream(oauth2ClientProperties.getPermitUrls()).noneMatch(x -> x.equals(uri))) {
                request = new CustomHeaderRequestWrapper(request, "Authorization", "Bearer " + oauth2ClientProperties.getSelfToken());
            } else {
                log.warn("PermitURL olduğu için token header eklenmedi");
            }

        } else {
            log.warn("SELF Token olmadığı için Header Eklenmedi");
        }
        filterChain.doFilter(request, response);
    }


    private static class CustomHeaderRequestWrapper extends HttpServletRequestWrapper {
        private final String headerName;
        private final String headerValue;

        public CustomHeaderRequestWrapper(HttpServletRequest request, String headerName, String headerValue) {
            super(request);
            this.headerName = headerName;
            this.headerValue = headerValue;
        }

        @Override
        public String getHeader(String name) {
            return (name.equalsIgnoreCase(headerName)) ? headerValue : super.getHeader(name);
        }
    }
}