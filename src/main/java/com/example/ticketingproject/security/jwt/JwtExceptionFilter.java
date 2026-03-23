package com.example.ticketingproject.security.jwt;

import com.example.ticketingproject.security.exception.CustomJwtException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {

            filterChain.doFilter(request, response);
        } catch (CustomJwtException e) {

            setErrorResponse(response, e);
        }
    }

    private void setErrorResponse(HttpServletResponse response, CustomJwtException e) throws IOException {
        response.setStatus(e.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> body = new HashMap<>();
        body.put("status", e.getHttpStatus().value());
        body.put("errorCode", e.getErrorStatusCode().getErrorCode());
        body.put("message", e.getErrorStatusCode().getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}