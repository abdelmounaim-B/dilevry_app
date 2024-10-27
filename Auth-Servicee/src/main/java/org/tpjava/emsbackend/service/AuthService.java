package org.tpjava.emsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tpjava.emsbackend.DTO.AuthRequest;
import org.tpjava.emsbackend.DTO.AuthResponse;
import org.tpjava.emsbackend.Utility.JwtUtil;
import org.tpjava.emsbackend.Utility.RestClientUtil;
import org.tpjava.emsbackend.exception.UnauthorizedException;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private RestClientUtil restClientUtil;

    @Autowired
    private JwtUtil jwtUtil;

    // Hardcoded roles and permissions for demonstration; replace with DB lookup or other data source in a real application
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String EMPLOYEE_ROLE = "EMPLOYEE";
    private static final List<String> ADMIN_PERMISSIONS = List.of("ADD_PRODUCT", "ADD_EMPLOYEE", "DELETE_ORDER");
    private static final List<String> EMPLOYEE_PERMISSIONS = List.of("ADD_ORDER", "GET_EMPLOYEE", "EMPLOYEE_VIEW");

    public AuthResponse authenticate(AuthRequest authRequest) {
        // Validate credentials using RestClientUtil
        boolean isValid = restClientUtil.validateCredentials(authRequest, authRequest.getUserType());
        System.out.println("isValid: " + isValid);
        if (!isValid) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String email = authRequest.getEmail();
        String role;
        List<String> permissions;

        // Set role and permissions based on the user type
        if (authRequest.getUserType().equalsIgnoreCase("ADMIN")) {
            role = ADMIN_ROLE;
            permissions = ADMIN_PERMISSIONS;
        } else {
            role = EMPLOYEE_ROLE;
            permissions = EMPLOYEE_PERMISSIONS;
        }

        // Generate token with role and permissions
        String token = jwtUtil.generateToken(email, role, permissions);
        System.out.println("\nGenerated token: " + token + "\n");

        return new AuthResponse(token);
    }


}
