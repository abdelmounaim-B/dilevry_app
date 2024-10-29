package org.tpjava.AuthService.controller;

import org.tpjava.AuthService.DTO.EmployeeDTO;
import org.tpjava.AuthService.annotation.PermissionRequired;
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


    @Value("${service.api-token}")
    private String authServiceApiToken;

    @PermissionRequired("EMPLOYEE_VIEW")
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {

        List<EmployeeDTO> employees = employeeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @PermissionRequired("EMPLOYEE_VIEW")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return ResponseEntity.ok(convertToDto(employee));
    }

    @PermissionRequired("EMPLOYEE_CREATE")
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedEmployee));
    }


    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setRegion(employeeDetails.getRegion());
        Employee updatedEmployee = employeeRepository.save(employee);

        return ResponseEntity.ok(convertToDto(updatedEmployee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id) {

        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } else {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
    }

    @PostMapping("/internal/validateEmployee")
    public ResponseEntity<?> validateEmployee(@RequestBody Map<String, String> credentials) {
        try {


            String email = credentials.get("email");
            String password = credentials.get("password");
            System.out.println("Validating credentials - Email: " + email + ", Password: " + password);

            // Find the employee based on credentials
            Optional<Employee> employee = employeeService.findByEmailAndPassword(email, password);

            // Return appropriate response based on validation
            if (employee.isPresent()) {
                System.out.println("Employee validation successful for: " + email);
                return ResponseEntity.ok(convertToDto(employee.get())); // Success response with EmployeeDTO
            } else {
                System.out.println("Employee validation failed - invalid credentials.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid credentials: Employee not found or incorrect password."); // Custom error message for invalid credentials
            }

        } catch (Exception e) {
            System.out.println("An error occurred during employee validation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal error occurred while processing the request. Please try again later."); // General error message
        }
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