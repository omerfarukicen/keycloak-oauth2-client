package com.demo.oauth2client.config.handler;

import com.demo.oauth2client.exception.ErrorAttributes;
import com.demo.oauth2client.exception.ErrorMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public final class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        final String messageID = UUID.randomUUID().toString();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (authException instanceof OAuth2AuthenticationException) {
            OAuth2Error error = ((OAuth2AuthenticationException) authException).getError();
            writeAuthLog(messageID, request, error.getDescription(), authException);
            response.getWriter().write(responseJson(messageID, "Geçersiz Token!", "Token imzasi geçersiz veya süresi geçmis"));
        }

        writeAuthLog(messageID, request, "Token Error", authException);
    }


    private void writeAuthLog(String messageID, HttpServletRequest request, String message, AuthenticationException authException) {
        log.error(String.format("MessageID: %s,  Request: %s, IP Adress: %s, Message: %s, Error: %s",
                messageID, request.getRequestURI(), request.getRemoteAddr(), message, authException.getMessage()));
    }

    @SneakyThrows
    private String responseJson(String messageID, String title, String message) {
        return objMapper.writeValueAsString(new ErrorMessages(messageID, new ErrorAttributes(title, message)));
    }


}
