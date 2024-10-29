package org.tpjava.Gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.tpjava.Gateway.annotation.PermissionRequired;
import org.tpjava.Gateway.util.JwtUtil;
import java.util.Collections;
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
        logForwardingRequest("Authentication Service", "/auth/login");
        try {
            String url = authServiceUrl + "/auth/login";
            ResponseEntity<Object> response = restTemplate.postForEntity(url, credentials, Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString().isEmpty() ? "Login failed: " + e.getStatusText() : e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login service encountered an internal error. Please try again later.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login. Please contact support.");
        }
    }

    @PermissionRequired("EMPLOYEE_VIEW")
    @GetMapping("/employees")
    public ResponseEntity<?> getEmployees(HttpServletRequest request) {
        logForwardingRequest("Employees Service", "/employees");
        String url = employeesServiceUrl + "/employees";
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(request));
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    @PermissionRequired("EMPLOYEE_VIEW")
    @GetMapping("/employees/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable Long id, HttpServletRequest request) {
        logForwardingRequest("Employees Service", "/employees/" + id);
        String url = employeesServiceUrl + "/employees/" + id;
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(request));
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    @PermissionRequired("EMPLOYEE_CREATE")
    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(@RequestBody Map<String, Object> employeeData, HttpServletRequest request) {
        logForwardingRequest("Employees Service", "/employees (POST)");
        String url = employeesServiceUrl + "/employees";
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(employeeData, createHeaders(request));
        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @PermissionRequired("EMPLOYEE_UPDATE")
    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(
                @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Map<String, Object> employeeDetails,
            HttpServletRequest request) {

        logForwardingRequest("Employees Service", "/employees/" + id + " (PUT)");
        String url = employeesServiceUrl + "/employees/" + id;
        HttpHeaders headers = createHeaders(request);
        headers.set("Authorization", token);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(employeeDetails, headers);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    @PermissionRequired("PRODUCT_VIEW")
    @GetMapping("/products/getAll")
    public ResponseEntity<?> getAllProducts(HttpServletRequest request) {
        logForwardingRequest("Products Service", "/products/getAll");
        String url = productsServiceUrl + "/products/getAll";
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(request));
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    private HttpHeaders createHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Collections.list(request.getHeaderNames()).forEach(headerName ->
                headers.set(headerName, request.getHeader(headerName))
        );
        return headers;
    }

    private void logForwardingRequest(String service, String endpoint) {
        System.out.println("Forwarding request to " + service + " endpoint: " + endpoint);
    }
}
