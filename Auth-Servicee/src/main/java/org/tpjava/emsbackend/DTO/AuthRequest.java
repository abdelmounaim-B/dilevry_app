package org.tpjava.emsbackend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private Long id;
    private String email;
    private String password;
    private String userType;
}
