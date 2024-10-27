package org.tpjava.emsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tpjava.emsbackend.model.Admin;
import org.tpjava.emsbackend.repository.AdminRepository;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }
    public Optional<Admin> findByEmailAndPassword(String email, String password) {
        return adminRepository.findByEmailAndPassword(email, password);
    }
}
