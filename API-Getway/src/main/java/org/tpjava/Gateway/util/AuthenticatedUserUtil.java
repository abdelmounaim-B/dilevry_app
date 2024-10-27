package org.tpjava.Gateway.util;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AuthenticatedUserUtil {

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
}
