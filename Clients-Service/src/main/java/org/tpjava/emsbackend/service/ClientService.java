package org.tpjava.emsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tpjava.emsbackend.model.Clients;
import org.tpjava.emsbackend.repository.ClientRepository;

import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Optional<Clients> findByEmailAndPassword(String email, String password) {
        return clientRepository.findByEmailAndPassword(email, password);
    }
}
