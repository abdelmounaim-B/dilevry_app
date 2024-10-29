package org.tpjava.emsbackend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientValidationResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String adress;
    private String phone;



}
