package org.tpjava.emsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tpjava.emsbackend.DTO.AuthRequest;
import org.tpjava.emsbackend.DTO.AuthResponse;
import org.tpjava.emsbackend.DTO.ClientValidationResponse;
import org.tpjava.emsbackend.Utility.JwtUtil;
import org.tpjava.emsbackend.Utility.RestClientUtil;
import org.tpjava.emsbackend.exception.UnauthorizedException;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private RestClientUtil restClientUtil;

    @Autowired
    private JwtUtil jwtUtil;

    // Define roles
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String EMPLOYEE_ROLE = "EMPLOYEE";
    private static final String CLIENT_ROLE = "CLIENT";

    // Define permissions for each role
// Define permissions for each role
    private static final List<String> ADMIN_PERMISSIONS = List.of(
            "ADD_PRODUCT", "EDIT_PRODUCT", "DELETE_PRODUCT", "VIEW_ALL_PRODUCTS",
            "ADD_EMPLOYEE", "EDIT_EMPLOYEE", "DELETE_EMPLOYEE", "EMPLOYEE_VIEW",
            "CREATE_ORDER", "EDIT_ORDER", "DELETE_ORDER", "VIEW_ALL_ORDERS"
    );

    private static final List<String> EMPLOYEE_PERMISSIONS = List.of(
            "EDIT_ORDER",  "VIEW_ORDERS", "EMPLOYEE_VIEW"
    );

    private static final List<String> CLIENT_PERMISSIONS = List.of(
            "VIEW_OWN_ORDERS", "CREATE_ORDER", "EDIT_OWN_ORDER", "DELETE_OWN_ORDER"
    );

    public AuthResponse authenticate(AuthRequest authRequest) {
        Optional<ClientValidationResponse> userOptional = restClientUtil.validateCredentials(authRequest, authRequest.getUserType());

        if (userOptional.isEmpty()) {
            throw new UnauthorizedException("Invalid credentials provided");
        }

        ClientValidationResponse user = userOptional.get();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String email = user.getEmail();
        String role;
        Long id = user.getId();
        List<String> permissions;

        // Assign role and permissions based on user type
        switch (authRequest.getUserType().toUpperCase()) {
            case "ADMIN":
                role = ADMIN_ROLE;
                permissions = ADMIN_PERMISSIONS;
                break;
            case "EMPLOYEE":
                role = EMPLOYEE_ROLE;
                permissions = EMPLOYEE_PERMISSIONS;
                break;
            case "CLIENT":
                role = CLIENT_ROLE;
                permissions = CLIENT_PERMISSIONS;
                break;
            default:
                throw new UnauthorizedException("Invalid user type");
        }

        // Generate the token
        String token = jwtUtil.generateToken(id, email, role, permissions);

        // Return both the token and user details in AuthResponse
        return new AuthResponse(token, id, email, role, firstName, lastName);
    }

}
