package com.demo.oauth2client.config;

import com.demo.oauth2client.session.SessionInfo;
import com.demo.oauth2client.session.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * Custom filter class responsible for extracting user information from the JWT and initializing the session.
 * This filter is executed once per incoming request to populate session information based on the JWT claims.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SessionFilter extends OncePerRequestFilter {

    private final SessionService sessionService;

    @Value("${spring.application.name}")
    private String applicationName;

    private void resolveToken(HttpServletRequest request) {
        sessionService.initRol();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            SessionInfo sessionInfo = sessionService.getSessionInfo();
            sessionService.getSessionInfo().setKullaniciAdi(applicationName);
        } else {
            Object obj = authentication.getPrincipal();
            sessionService.getSessionInfo().setKullaniciAdi(authentication.getName());
            if (obj instanceof Jwt jwt) {
                Map<String, Object> claims = jwt.getClaims();
                SessionInfo sessionInfo = sessionService.getSessionInfo();
                sessionInfo.setAdi((String) claims.get("given_name"));
                sessionInfo.setEmail((String) claims.get("email"));
                sessionInfo.setSoyadi((String) claims.get("family_name"));
                sessionInfo.setGruplar((Map<String, Map<String, String>>) claims.get("groups"));
                sessionInfo.setIslemGruplari((Map<String, Map<String, String>>) claims.get("action_groups"));
                sessionInfo.setKullaniciOzellikleri((Map<String, String>) claims.get("user_attributes"));
                MDC.put("SESSION_INFO", sessionInfo.sessionShortInfo());
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            resolveToken(request);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("SESSION_INFO");
        }
    }

}
