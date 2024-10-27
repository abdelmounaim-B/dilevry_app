package org.tpjava.AuthService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tpjava.AuthService.model.Employee;
import org.tpjava.AuthService.repository.EmployeeRepository;

import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Optional<Employee> findByEmailAndPassword(String email, String password) {
        return employeeRepository.findByEmailAndPassword(email, password);
    }
}
