package org.tpjava.emsbackend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String firstName;
    private String lastName;
    private String token;
    private Long id;
    private String email;
    private String role;

    public AuthResponse(String token, Long id, String email, String role, String firstName, String lastName) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;

    }

}