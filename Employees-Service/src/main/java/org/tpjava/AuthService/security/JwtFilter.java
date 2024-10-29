package org.tpjava.AuthService.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tpjava.AuthService.util.JwtUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.extractAllClaims(token);

                // Extract email, role, and permissions from token
                Long ID = claims.get("ID", Long.class);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                List<String> permissions = claims.get("permissions", List.class);

                System.out.println("Authenticated user ID: " + ID);
                System.out.println("Authenticated user: " + email);
                System.out.println("Role: " + role);
                System.out.println("Permissions: " + permissions);

                // Convert permissions to GrantedAuthority objects for Spring Security
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));  // Add role as ROLE_xxx
                authorities.addAll(permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));

                // Create an authentication token with authorities and set it in the SecurityContext
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("Token is invalid for " + requestURI);
            }
        // Continue with the rest of the filter chain
        filterChain.doFilter(request, response);

        // Clear the SecurityContext after request processing is complete
        SecurityContextHolder.clearContext();
    }
}
