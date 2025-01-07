package org.example.servicepaymenttransaction.Controllers;

import org.example.servicepaymenttransaction.Models.Compte;
import org.example.servicepaymenttransaction.Models.Hssab;
import org.example.servicepaymenttransaction.Services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;


@RestController
@RequestMapping("/comptes")
public class CompteController {

    @Autowired
    private CompteService compteService;

    // Endpoint pour créer un compte avec un solde initial
    @PostMapping("/creer")
    public ResponseEntity<Compte> creerCompte(@RequestParam Long userId,
                                              @RequestParam BigDecimal soldeInitial) {

        Compte compte = compteService.creerCompte(userId, soldeInitial);
        return ResponseEntity.ok(compte);
    }

    // Endpoint pour supprimer un compte par son ID
    @DeleteMapping("/supprimer/{compteId}")
    public ResponseEntity<String> supprimerCompte(@PathVariable Long compteId) {
        compteService.supprimerCompte(compteId);
        return ResponseEntity.ok("Compte supprimé avec succès");
    }
    // Gestion des exceptions
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @GetMapping("/user/{userId}")
    public Compte getCompteByUserId(@PathVariable Long userId) {
        Compte compte = compteService.getCompteByUserId(userId);
        System.out.println(compte.toString());
        return  compte;
    }

    @PutMapping("/add-virtual-card")
    public ResponseEntity<String> addVirtualCardByUserId(
            @RequestParam Long userId,
            @RequestParam String newCard) {
        Compte updatedCompte = compteService.addVirtualCard(userId, newCard);
        return ResponseEntity.ok("success");
    }

    @PutMapping("/debit/{userId}")
    public ResponseEntity<String> debitCompte(@PathVariable Long userId, @RequestParam BigDecimal amount) {
        try {
            boolean success = compteService.debitCompte(userId, amount);
            if (success) {
                return ResponseEntity.ok("Amount debited successfully: " + amount + " MAD");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Insufficient balance for debit operation.");
            }
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during debit operation: " + ex.getMessage());
        }
    }
}











