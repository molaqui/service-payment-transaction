package org.example.servicepaymenttransaction.Repositories;

import org.example.servicepaymenttransaction.Models.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompteRepository extends JpaRepository<Compte, Long> {


    Optional<Compte> findByUserId(Long userId);

    Optional<Compte> findByTelephone(String telephone); // Recherche par téléphone
}
