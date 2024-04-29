package com.demo.oauth2client.config.handler;

import com.demo.oauth2client.exception.ErrorAttributes;
import com.demo.oauth2client.exception.ErrorMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public final class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        final String authenticationUser = SecurityContextHolder.getContext().getAuthentication().getName();
        final String messageID = UUID.randomUUID().toString();
        writeAuthLog(messageID, request, "Yetki Aşımı", authenticationUser, accessDeniedException);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(responseJson(messageID, "Erişim Engeli!", "Erişmek istediğiniz method için yetkiniz bulunmamaktadır."));
    }

    private void writeAuthLog(String messageID, HttpServletRequest request, String message, String user, AccessDeniedException authException) {
        log.error(String.format("MessageID: %s,  Request: %s, IP Adress: %s,User: %s, Message: %s,  Error: %s",
                messageID, request.getRequestURI(), request.getRemoteAddr(), user, message, authException.getMessage()));
    }

    @SneakyThrows
    private String responseJson(String messageID, String title, String message) {
        return objMapper.writeValueAsString(new ErrorMessages(messageID, new ErrorAttributes(title, message)));
    }


}
