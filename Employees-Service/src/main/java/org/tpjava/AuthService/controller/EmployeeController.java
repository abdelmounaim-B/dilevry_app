package org.tpjava.AuthService.controller;

import org.tpjava.AuthService.DTO.EmployeeDTO;
import org.tpjava.AuthService.exception.ResourceNotFoundException;
import org.tpjava.AuthService.model.Employee;
import org.tpjava.AuthService.repository.EmployeeRepository;
import org.tpjava.AuthService.service.EmployeeService;
import org.tpjava.AuthService.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${auth.service.api-token}")
    private String authServiceApiToken;

    // Get all employees - JWT protected
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(@RequestHeader("Authorization") String token) {

        List<EmployeeDTO> employees = employeeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    // Create a new employee - JWT protected
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestHeader("Authorization") String token, @RequestBody Employee employee) {
        System.out.println("Token validation successful for Create Employee.");
        Employee savedEmployee = employeeRepository.save(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedEmployee));
    }

    // Get employee by id - JWT protected
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id, @RequestHeader("Authorization") String token) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return ResponseEntity.ok(convertToDto(employee));
    }

    // Update employee - JWT protected
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Employee employeeDetails) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setRegion(employeeDetails.getRegion());
        Employee updatedEmployee = employeeRepository.save(employee);

        return ResponseEntity.ok(convertToDto(updatedEmployee));
    }

    // Delete employee - JWT protected
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id, @RequestHeader("Authorization") String token) {

        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
    }

    // Internal API for validating employees - API token protected
    @PostMapping("/internal/validateEmployee")
    public ResponseEntity<EmployeeDTO> validateEmployee(@RequestBody Map<String, String> credentials,
                                                        @RequestHeader("Authorization") String apiToken) {

        String email = credentials.get("email");
        String password = credentials.get("password");
        System.out.println("Validating credentials - Email: " + email + ", Password: " + password);

        Optional<Employee> employee = employeeService.findByEmailAndPassword(email, password);
        return employee.map(value -> ResponseEntity.ok(convertToDto(value)))
                .orElseGet(() -> {
                    System.out.println("Employee validation failed - invalid credentials.");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                });
    }

    private EmployeeDTO convertToDto(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setRegion(employee.getRegion());
        return dto;
    }
}
