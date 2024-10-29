package org.tpjava.emsbackend.Utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.tpjava.emsbackend.DTO.AuthRequest;
import org.tpjava.emsbackend.DTO.ClientValidationResponse;

import java.util.Optional;

@Component
public class RestClientUtil {

    @Value("${auth.service.api-token}")
    private String authServiceApiToken;

    @Value("${service.url}")
    private String serviceUrl;


    @Value("${client.service.action}")
    private String clientServiceAction;

    @Value("${client.service.port}")
    private String clientServicePort;

    @Value("${client.service.path}")
    private String clientServicePath;

    @Value("${employee.service.action}")
    private String employeeServiceAction;


    @Value("${employee.service.port}")
    private String employeeServicePort;

    @Value("${employee.service.path}")
    private String employeeServicePath;

    @Value("${admin.service.action}")
    private String adminServiceAction;


    @Value("${admin.service.port}")
    private String adminServicePort;

    @Value("${admin.service.path}")
    private String adminServicePath;

    private final RestTemplate restTemplate = new RestTemplate();

    public Optional<ClientValidationResponse> validateCredentials(AuthRequest authRequest, String role) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("service.api-token", authServiceApiToken);
        headers.set("Authorization", authServiceApiToken);


        System.out.println("Validating credentials for " + role + " with email: " + authRequest.getEmail());

        HttpEntity<AuthRequest> request = new HttpEntity<>(authRequest, headers);
        String validationUrl = getValidationUrl(role);

        if (validationUrl == null) {
            System.out.println("Error: Invalid role provided.");
            return Optional.empty();
        }

        System.out.println("Validation URL: " + validationUrl);

        try {
            ResponseEntity<ClientValidationResponse> response = restTemplate.exchange(
                    validationUrl, HttpMethod.POST, request, ClientValidationResponse.class);
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException.Unauthorized e) {
            System.out.println("Unauthorized: Invalid credentials provided.");
        } catch (HttpClientErrorException e) {
            System.out.println("Error during client validation: " + e.getMessage());
        }

        return Optional.empty();
    }



    // Helper method to construct the URL based on the role
    private String getValidationUrl(String role) {
        switch (role.toUpperCase()) {
            case "CLIENT":
                return "http://" + serviceUrl + ":" + clientServicePort + "/" + clientServicePath + "/internal/validate" +clientServiceAction;
            case "EMPLOYEE":
                return "http://" + serviceUrl + ":" + employeeServicePort + "/" + employeeServicePath + "/internal/validate" +employeeServiceAction;
            case "ADMIN":
                return "http://" + serviceUrl + ":" + adminServicePort + "/" + adminServicePath + "/internal/validate" +adminServiceAction;
            default:
                return null;
        }
    }
}
