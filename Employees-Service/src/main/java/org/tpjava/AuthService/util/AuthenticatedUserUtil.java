package org.tpjava.AuthService.util;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AuthenticatedUserUtil {

    private static JwtUtil jwtUtil;

    // Set JwtUtil via dependency injection
    public AuthenticatedUserUtil(JwtUtil jwtUtil) {
        AuthenticatedUserUtil.jwtUtil = jwtUtil;
    }

    public static String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Returns email as the principal
        }
        return null;
    }

    public static List<String> getAuthenticatedUserPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .filter(auth -> auth.getAuthority().startsWith("ROLE_"))
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority().substring(5)) // Remove "ROLE_" prefix
                    .orElse(null);
        }
        return null;
    }

    public static Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getCredentials() != null) {
            String token = authentication.getCredentials().toString();
            Claims claims = jwtUtil.extractAllClaims(token);
            return claims.get("ID", Long.class);
        }
        return null;
    }

}
