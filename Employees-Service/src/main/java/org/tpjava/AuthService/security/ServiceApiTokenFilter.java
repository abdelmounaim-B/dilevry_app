package org.tpjava.AuthService.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ServiceApiTokenFilter extends OncePerRequestFilter {

    @Value("${service.api-token}")
    private String validServiceApiToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String serviceApiToken = request.getHeader("service.api-token");

        // Check if the token is present and matches the expected value
        if (serviceApiToken == null || !serviceApiToken.equals(validServiceApiToken)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access denied: Invalid service API token");
            return;
        }

        // If the token is valid, continue with the request
        filterChain.doFilter(request, response);
    }
}
