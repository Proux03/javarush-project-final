package com.javarush.jira.common.internal.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@AllArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = extractToken(request); // Извлекаем токен из заголовка Authorization

            if (token != null && !token.isEmpty()) {
                Authentication authResult = jwtAuthenticationProvider.authenticate(new BearerTokenAuthenticationToken(token));

                if (authResult != null && authResult.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(authResult);
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка фильтрации JWT токена.", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Метод для извлечения JWT-токена из заголовков запроса.
     */
    private String extractToken(HttpServletRequest request) {
        String bearerHeader = request.getHeader("Authorization");
        if (bearerHeader != null && bearerHeader.startsWith("Bearer ")) {
            return bearerHeader.substring(7); // Удаляем префикс 'Bearer '
        }
        return null;
    }
}