package org.tpjava.emsbackend.controller;

import org.tpjava.emsbackend.exception.ResourceNotFoundException;
import org.tpjava.emsbackend.model.Clients;
import org.tpjava.emsbackend.repository.ClientRepository;
import org.tpjava.emsbackend.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/clients")
public class ClientsController {

    @Autowired
    private ClientRepository clientRepository;

    private ClientService clientService;

    // API token for inter-service communication
    @Value("${auth.service.api-token}")
    private String authServiceApiToken;

    @Autowired
    public ClientsController(ClientService clientService) {
        this.clientService = clientService;
    }

    // Public endpoint: Get all clients
    @GetMapping
    public List<Clients> getAllClientes() {
        return clientRepository.findAll();
    }

    // Public endpoint: Create a new client
    @PostMapping
    public ResponseEntity<Clients> createCliente(@RequestBody Clients clients) {
        Clients savedClients = clientRepository.save(clients);
        return ResponseEntity.ok(savedClients);
    }

    // Public endpoint: Get client by ID
    @GetMapping("/{id}")
    public ResponseEntity<Clients> getClienteById(@PathVariable Long id) {
        Clients clients = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not exist with id: " + id));
        return ResponseEntity.ok(clients);
    }

    // Public endpoint: Update client
    @PutMapping
    public ResponseEntity<Clients> updateCliente(@RequestBody Clients clientsDetails) {
        Clients clients = clientRepository.findById(clientsDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not exist with id: " + clientsDetails.getId()));

        clients.setFirstName(clientsDetails.getFirstName());
        clients.setLastName(clientsDetails.getLastName());
        clients.setEmail(clientsDetails.getEmail());
        clients.setPassword(clientsDetails.getPassword());
        clients.setAdress(clientsDetails.getAdress());
        Clients updatedClients = clientRepository.save(clients);
        return ResponseEntity.ok(updatedClients);
    }

    // Public endpoint: Delete client
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteCliente(@PathVariable Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Private endpoint: Validate client credentials (only accessible by Auth Service)
    @PostMapping("/internal/validateClient")
    public ResponseEntity<Clients> validateClient(@RequestBody Map<String, String> credentials,
                                                  @RequestHeader("Authorization") String apiToken) {
        // Verify the API token
        if (!apiToken.equals(authServiceApiToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extract email and password from request body
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<Clients> client = clientService.findByEmailAndPassword(email, password);
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
