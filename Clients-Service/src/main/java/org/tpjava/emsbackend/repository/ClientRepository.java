package org.tpjava.emsbackend.repository;

import org.tpjava.emsbackend.model.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Clients, Long> {
    Optional<Clients> findByEmailAndPassword(String email, String password);
}
