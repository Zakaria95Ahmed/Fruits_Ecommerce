package com.fruits.ecommerce.configuration.SecurityConfig.JWT_Filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fruits.ecommerce.configuration.SecurityConfig.SecurityCore.SecurityProperties;
import com.fruits.ecommerce.exceptions.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@Component@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final SecurityProperties securityProperties;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {
        // Log detailed information about the request
        log.error("Access denied error for request to URI: {}", request.getRequestURI());
        log.error("Request method: {}", request.getMethod());
        log.error("Error message: {}", exception.getMessage());

        // Log headers for additional debugging information
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.debug("Header '{}': {}", headerName, request.getHeader(headerName));
        }

        // Set response properties
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());

        // Create and write the custom HTTP response
        HttpResponse httpResponse = new HttpResponse(
                FORBIDDEN.value(),
                FORBIDDEN,
                FORBIDDEN.getReasonPhrase().toUpperCase(),
                securityProperties.getAccessDeniedMessage()
        );

        new ObjectMapper().writeValue(response.getOutputStream(), httpResponse);
    }
}
