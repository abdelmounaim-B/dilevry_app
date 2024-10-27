package org.tpjava.Gateway.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.tpjava.Gateway.util.AuthenticatedUserUtil;
import org.tpjava.Gateway.util.JwtUtil;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class GatewayController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.clients.url}")
    private String clientsServiceUrl;

    @Value("${service.Authentification.url}")
    private String authServiceUrl;

    @Value("${service.employees.url}")
    private String employeesServiceUrl;

    @Value("${service.products.url}")
    private String productsServiceUrl;


    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        System.out.println("Forwarding /auth/login request to the authentication service.");
        String url = authServiceUrl + "/auth/login";  // authServiceUrl is the base URL for the auth service
        return restTemplate.postForEntity(url, credentials, Object.class);
    }

    @GetMapping("/employees")
    public ResponseEntity<?> getEmployees(HttpServletRequest request) {
        List<String> userPermissions = AuthenticatedUserUtil.getAuthenticatedUserPermissions();
        String requiredPermission = "EMPLOYEE_VIEW"; // replace with the required permission

        // Check if the user has the required permission
        if (!userPermissions.contains(requiredPermission)) {
            System.out.println("User lacks required permission: " + requiredPermission);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view employees.");
        }

        System.out.println("User permissions: " + userPermissions);
        System.out.println("Forwarding /employees request to the employees service.");

        // Construct the URL for the employees service
        String url = employeesServiceUrl + "/employees";  // Replace with the actual employees service base URL

        // Extract Authorization header from the incoming request
        HttpHeaders headers = new HttpHeaders();
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            headers.set("Authorization", authorizationHeader);
            System.out.println("Authorization Header: " + authorizationHeader);  // Log the token
        } else {
            System.out.println("No Authorization header found in the request.");
        }

        // Create an HttpEntity with the headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Use RestTemplate to forward the GET request with headers
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Forwarding " + url + " request to the employees service.");

        // Return the response body from the employees service
        return ResponseEntity.ok(response.getBody());
    }


    @GetMapping("/employees/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable Long id, HttpServletRequest request) {
        System.out.println("Forwarding /employees/id request to the employees service.");

        // Construct the URL for the employees service
        String url = employeesServiceUrl+ "/employees/" + id;  // Replace with the actual employees service base URL

        // Extract Authorization header from the incoming request
        HttpHeaders headers = new HttpHeaders();
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            headers.set("Authorization", authorizationHeader);
            System.out.println("Authorization Header: " + authorizationHeader);  // Log the token
        } else {
            System.out.println("No Authorization header found in the request.");
        }

        // Create an HttpEntity with the headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Use RestTemplate to forward the GET request with headers
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Forwarding " + url + " request to the employees service.");

        // Return the response body from the employees service
        return ResponseEntity.ok(response.getBody());
    }

    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(@RequestBody Map<String, Object> employeeData, HttpServletRequest request) {
        System.out.println("Forwarding /employees POST request to the employees service for creating a new employee.");

        // Construct the URL for the employees service
        String url = employeesServiceUrl + "/employees";  // Make sure employeesServiceUrl is defined as the base URL for employees service

        // Extract Authorization header from the incoming request
        HttpHeaders headers = new HttpHeaders();
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            headers.set("Authorization", authorizationHeader);
            System.out.println("Authorization Header: " + authorizationHeader);  // Log the token
        } else {
            System.out.println("No Authorization header found in the request.");
        }

        // Create an HttpEntity with the headers and employee data
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(employeeData, headers);

        // Use RestTemplate to forward the POST request with headers and body
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        System.out.println("Forwarding " + url + " POST request to the employees service.");

        // Return the response body from the employees service
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Map<String, Object> employeeDetails,
            HttpServletRequest request) {

        System.out.println("Forwarding /employees/{id} PUT request to the employees service for updating an employee.");

        // Construct the URL for the employees service
        String url = employeesServiceUrl + "/employees/" + id;

        // Set up headers, including the Authorization token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token); // Forward the original token
        System.out.println("Authorization Header: " + token);  // Log the token

        // Create an HttpEntity with headers and request body
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(employeeDetails, headers);

        // Use RestTemplate to forward the PUT request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        System.out.println("Forwarding " + url + " PUT request to the employees service.");

        // Return the response body and status code from the employees service
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @GetMapping("/products/getAll")
    public ResponseEntity<?> getAllProducts(HttpServletRequest request) {
        System.out.println("Forwarding /products/getAll request to the employees service.");

        // Construct the URL for the employees service
        String url = productsServiceUrl + "/products/getAll";  // Replace with the actual employees service base URL

        // Extract Authorization header from the incoming request
        HttpHeaders headers = new HttpHeaders();
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            headers.set("Authorization", authorizationHeader);
            System.out.println("Authorization Header: " + authorizationHeader);  // Log the token
        } else {
            System.out.println("No Authorization header found in the request.");
        }

        // Create an HttpEntity with the headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Use RestTemplate to forward the GET request with headers
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println("Forwarding " + url + " request to the product service.");

        // Return the response body from the employees service
        return ResponseEntity.ok(response.getBody());
    }
}
