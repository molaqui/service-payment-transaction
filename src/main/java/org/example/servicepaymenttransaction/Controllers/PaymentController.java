package org.example.servicepaymenttransaction.Controllers;

import org.example.servicepaymenttransaction.Feign.UserServiceClient;
import org.example.servicepaymenttransaction.Models.Compte;
import org.example.servicepaymenttransaction.Models.Transaction;
import org.example.servicepaymenttransaction.Repositories.CompteRepository;
import org.example.servicepaymenttransaction.Services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/paiements")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Qualifier("org.example.servicepaymenttransaction.Feign.UserServiceClient")
    @Autowired
    private UserServiceClient userServiceClient;

    @PostMapping("/{compteId}/payer-facture/{creancierId}")
    public ResponseEntity<Transaction> payerFacture(@PathVariable Long compteId,
                                                    @PathVariable Long creancierId,
                                                    @RequestParam BigDecimal montant) {
        return ResponseEntity.ok(paymentService.payerFacture(compteId, montant, creancierId));
    }

    @PostMapping("/{compteId}/payer")
    public ResponseEntity<Transaction> payer(@PathVariable Long compteId, @RequestParam BigDecimal montant, @RequestParam String typePaiement) {
        return ResponseEntity.ok(paymentService.effectuerPaiement(compteId, montant, typePaiement));
    }

    @PostMapping("/{compteId}/recevoir")
    public ResponseEntity<Transaction> recevoir(@PathVariable Long compteId, @RequestParam BigDecimal montant) {
        return ResponseEntity.ok(paymentService.recevoirPaiement(compteId, montant));
    }

    @PostMapping("/{sourceId}/transfert/{destinationId}")
    public ResponseEntity<List<Transaction>> transferer(@PathVariable Long sourceId,
                                                        @PathVariable Long destinationId,
                                                        @RequestParam BigDecimal montant) {
        return ResponseEntity.ok(paymentService.transfererArgent(sourceId, destinationId, montant));
    }


    @PostMapping("/{compteId}/toCard")
    public ResponseEntity<String> transferToCard(@PathVariable Long compteId, @RequestParam BigDecimal montant) {
        paymentService.effectuerPaiement(compteId, montant, "transfer vers carte virtuelle");
        return ResponseEntity.ok("sold transferred to card");
    }

//    @GetMapping("/{compteId}/solde")
//    public ResponseEntity<BigDecimal> consulterSolde(@PathVariable Long compteId) {
//        return ResponseEntity.ok(paymentService.consulterSolde(compteId));
//    }

//    @GetMapping("/{compteId}/transactions")
//    public ResponseEntity<List<Transaction>> listerTransactions(@PathVariable Long compteId) {
//        return ResponseEntity.ok(paymentService.listerTransactions(compteId));
//    }
// Endpoint pour lister les transactions par ID utilisateur

    // Endpoint pour lister les transactions par ID utilisateur
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Transaction>> listerTransactionsParUserId(@PathVariable Long userId) {
//        List<Transaction> transactions = paymentService.listerTransactionsParUserId(userId);
//        return ResponseEntity.ok(transactions);
//    }

//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Map<String, Object>>> listerTransactionsParUserId(@PathVariable Long userId) {
//        List<Transaction> transactions = paymentService.listerTransactionsParUserId(userId);
//        List<Map<String, Object>> transactionsWithUserNames = transactions.stream().map(transaction -> {
//            Map<String, Object> transactionMap = new HashMap<>();
//            transactionMap.put("description", transaction.getDescription());
//            transactionMap.put("montant", transaction.getMontant());
//            transactionMap.put("date", transaction.getDate());
//            transactionMap.put("statut", transaction.getStatut());
//
//            // Ajouter le nom des utilisateurs source et destination
//            String sourceUserName = userServiceClient.getClientById(transaction.getSourceUserId()).get("nom").toString();
//            String destinationUserName = userServiceClient.getClientById(transaction.getDestinationUserId()).get("nom").toString();
//
//            transactionMap.put("sourceUserName", sourceUserName);
//            transactionMap.put("destinationUserName", destinationUserName);
//
//            return transactionMap;
//        }).toList();
//
//        return ResponseEntity.ok(transactionsWithUserNames);
//    }



    // Gestion globale des exceptions pour afficher un message clair
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<
            Map<String, String>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Endpoint pour vérifier un client par son ID
    @GetMapping("/clients/{id}")
    public ResponseEntity<Map<String, Object>> verifierClient(@PathVariable Long id) {
        Map<String, Object> client = userServiceClient.getClientById(id);
        if (client == null || client.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(client);
    }
    @GetMapping("/solde/user/{userId}")
    public ResponseEntity<BigDecimal> consulterSoldeParUserId(@PathVariable Long userId) {
        BigDecimal solde = paymentService.calculerSoldeParUserId(userId);
        return ResponseEntity.ok(solde);
    }

    @PostMapping("/user/{userId}/ajouter-solde")
    public ResponseEntity<BigDecimal> ajouterMontantAuSolde(@PathVariable Long userId, @RequestParam BigDecimal montant) {
        BigDecimal nouveauSolde = paymentService.ajouterMontantAuSolde(userId, montant);
        return ResponseEntity.ok(nouveauSolde);
    }



//    @PostMapping("/transferer-par-telephone")
//    public ResponseEntity<String> transfererParTelephone(
//            @RequestParam Long userIdSource,
//            @RequestParam String telephoneDestination,
//            @RequestParam BigDecimal montant) {
//        try {
//            paymentService.transfererParTelephone(userIdSource, telephoneDestination, montant);
//            return ResponseEntity.ok("Transfert effectué avec succès.");
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
@PostMapping("/transferer-par-telephone")
public ResponseEntity<Map<String, String>> transfererParTelephone(@RequestBody Map<String, Object> requestData) {
    Long userIdSource = Long.valueOf(requestData.get("userIdSource").toString());
    String telephoneDestination = requestData.get("telephoneDestination").toString();
    BigDecimal montant = new BigDecimal(requestData.get("montant").toString());

    try {
        paymentService.transfererParTelephone(userIdSource, telephoneDestination, montant);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Transfert effectué avec succès.");
        return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}

    @GetMapping("/test-client/{id}")
    public ResponseEntity<Map<String, Object>> testClient(@PathVariable Long id) {
        try {
            Map<String, Object> client = userServiceClient.getClientById(id);
            return ResponseEntity.ok(client);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @GetMapping("/user/current/transactions")
    public ResponseEntity<List<Transaction>> listerTransactionsUtilisateurConnecte(@RequestHeader("userId") Long userId) {
        List<Transaction> transactions = paymentService.listerTransactionsParUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<Transaction>> listerTransactionsParUserId(@PathVariable Long userId) {
        List<Transaction> transactions = paymentService.listerTransactionsParUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    @Autowired
    private CompteRepository compteRepo;

    @GetMapping("/user/current/phone")
    public ResponseEntity<Map<String, String>> getCurrentUserPhone(@RequestHeader("userId") Long userId) {
        System.out.println("userId reçu : " + userId); // Ajoutez ce log
        try {
            Compte compte = compteRepo.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Compte introuvable pour l'utilisateur ID : " + userId));

            System.out.println("Compte trouvé : " + compte); // Ajoutez ce log
            Map<String, String> response = new HashMap<>();
            response.put("telephone", compte.getTelephone());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du téléphone : " + e.getMessage()); // Ajoutez ce log
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}


