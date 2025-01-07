package org.example.servicepaymenttransaction.Repositories;

import feign.Param;
import org.example.servicepaymenttransaction.Models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
//    List<Transaction> findByCompteId(Long compteId);
// Trouver les transactions par ID utilisateur

List<Transaction> findByCompteUserId(Long userId);
    // Récupérer toutes les transactions où userId est soit la source, soit la destination
    @Query("SELECT t FROM Transaction t WHERE t.sourceUserId = :userId OR t.destinationUserId = :userId")
    List<Transaction> findBySourceUserIdOrDestinationUserId(@Param("userId") Long userId, Long id);
}
