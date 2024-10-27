package org.tpjava.emsbackend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.tpjava.emsbackend.exception.ResourceNotFoundException;
import org.tpjava.emsbackend.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tpjava.emsbackend.repository.AdminRepository;
import org.tpjava.emsbackend.service.AdminService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;
    private AdminService adminService;

    @Value("${auth.service.api-token}")
    private String authServiceApiToken;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Get all employees
    @GetMapping
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // Create a new employee
    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin savedAdmin = adminRepository.save(admin);
        return ResponseEntity.ok(savedAdmin); // You can return a 201 status here as well if preferred
    }

    // Get employee by id
    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not exist with id: " + id));
        return ResponseEntity.ok(admin);
    }

    // Update employee
    @PutMapping
    public ResponseEntity<Admin> updateAdmin(@RequestBody Admin adminDetails) {
        Admin admin = adminRepository.findById(adminDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not exist with id: " + adminDetails.getId()));

        // Update employee details
        admin.setFirstName(adminDetails.getFirstName());
        admin.setLastName(adminDetails.getLastName());
        admin.setEmail(adminDetails.getEmail());
        admin.setPassword(adminDetails.getPassword());
        admin.setRole(adminDetails.getRole());
        Admin updatedAdmin = adminRepository.save(admin);
        return ResponseEntity.ok(updatedAdmin);
    }

    // Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteAdmin(@PathVariable Long id) {
        if (adminRepository.existsById(id)){
            adminRepository.deleteById(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        }else {
            return (ResponseEntity<Map<String, Boolean>>) ResponseEntity.notFound();
        }

    }

    @PostMapping("/internal/validateAdmin")
    public ResponseEntity<Admin> validateClient(@RequestBody Map<String, String> credentials,
                                                   @RequestHeader("Authorization") String apiToken) {
        // Verify the API token
        if (!apiToken.equals(authServiceApiToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extract email and password from request body
        String email = credentials.get("email");
        String password = credentials.get("password");

        System.out.println("Validating credentials for Admin with email: " + email);

        Optional<Admin> client = adminService.findByEmailAndPassword(email, password);
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
