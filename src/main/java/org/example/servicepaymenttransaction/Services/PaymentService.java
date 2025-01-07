package org.example.servicepaymenttransaction.Services;

import org.example.servicepaymenttransaction.Feign.UserServiceClient;
import org.example.servicepaymenttransaction.Models.Compte;
import org.example.servicepaymenttransaction.Models.Transaction;
import org.example.servicepaymenttransaction.Repositories.CompteRepository;
import org.example.servicepaymenttransaction.Repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    private CompteRepository compteRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @Qualifier("org.example.servicepaymenttransaction.Feign.UserServiceClient")
    @Autowired
    private UserServiceClient userServiceClient;
    // Simuler une liste de créanciers
    private static final Map<Long, String> CREANCIERS = new HashMap<>() {{
        put(1L, "Electricité Nationale");
        put(2L, "Compagnie des Eaux");
        put(3L, "Opérateur Télécom");
    }};

    // Payer une facture à un créancier
    public Transaction payerFacture(Long compteId, BigDecimal montant, Long creancierId) {
        Compte compte = compteRepo.findById(compteId)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        String nomCreancier = CREANCIERS.get(creancierId);
        if (nomCreancier == null) {
            throw new RuntimeException("Créancier non trouvé");
        }

        if (compte.getSolde().compareTo(montant) < 0) {
            throw new RuntimeException("Solde insuffisant. Solde actuel : "
                    + compte.getSolde() + ", Montant demandé : " + montant);
        }

        // Débiter le solde du compte
        compte.setSolde(compte.getSolde().subtract(montant));
        compteRepo.save(compte);

        // Créer la transaction pour le paiement du créancier
        Transaction transaction = new Transaction();
        transaction.setMontant(montant.negate());
        transaction.setType("paiement");
        transaction.setDescription("Paiement effectué au créancier : " + nomCreancier);
        transaction.setDate(new Date());
        transaction.setStatut("effectuée");
        transaction.setCompte(compte);
        transaction.setSourceUserId(compte.getUserId()); // Définir sourceUserId
        transaction.setDestinationUserId(creancierId);   // Définir destinationUserId

        return transactionRepo.save(transaction);
    }


    // Effectuer un paiement
    public Transaction effectuerPaiement(Long compteId, BigDecimal montant, String typePaiement) {
        Compte compte = compteRepo.findById(compteId)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        if (compte.getSolde().compareTo(montant) < 0) {
            throw new RuntimeException("Solde insuffisant. Solde actuel : "
                    + compte.getSolde() + ", Montant demandé : " + montant);
        }

        // Mettre à jour le solde du compte
        compte.setSolde(compte.getSolde().subtract(montant));
        compteRepo.save(compte);

        // Créer une transaction avec sourceUserId défini
        Transaction transaction = new Transaction();
        transaction.setMontant(montant.negate());
        transaction.setType(typePaiement);
        transaction.setDescription("Paiement effectué");
        transaction.setDate(new Date());
        transaction.setStatut("effectuée");
        transaction.setCompte(compte);
        transaction.setSourceUserId(compte.getUserId()); // Définir le sourceUserId

        return transactionRepo.save(transaction);
    }


    // Réception de fonds
    public Transaction recevoirPaiement(Long compteId, BigDecimal montant) {
        Compte compte = compteRepo.findById(compteId).orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        compte.setSolde(compte.getSolde().add(montant));
        compteRepo.save(compte);

        Transaction transaction = new Transaction();
        transaction.setMontant(montant); // Enregistrer en positif
        transaction.setType("reception");
        transaction.setDescription("Fonds reçus");
        transaction.setDate(new Date());
        transaction.setStatut("effectuée");
        transaction.setCompte(compte);

        return transactionRepo.save(transaction);
    }

    // Méthode pour transférer de l'argent entre deux comptes
    public List<Transaction> transfererArgent(Long sourceId, Long destinationId, BigDecimal montant) {
        Compte source = compteRepo.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("Compte source non trouvé"));
        Compte destination = compteRepo.findById(destinationId)
                .orElseThrow(() -> new RuntimeException("Compte destination non trouvé"));

        if (source.getSolde().compareTo(montant) < 0) {
            throw new RuntimeException("Solde insuffisant. Solde actuel : " + source.getSolde() + ", Montant demandé : " + montant);
        }

        // Débiter le compte source
        source.setSolde(source.getSolde().subtract(montant));
        compteRepo.save(source);

        Transaction transactionDebit = new Transaction();
        transactionDebit.setMontant(montant.negate());
        transactionDebit.setType("transfert");
        transactionDebit.setDescription("Transfert à l'utilisateur ID : " + destination.getUserId());
        transactionDebit.setDate(new Date());
        transactionDebit.setStatut("effectuée");
        transactionDebit.setCompte(source);
        transactionDebit.setSourceUserId(source.getUserId());
        transactionDebit.setDestinationUserId(destination.getUserId());
        transactionRepo.save(transactionDebit);

        // Créditer le compte destination
        destination.setSolde(destination.getSolde().add(montant));
        compteRepo.save(destination);

        Transaction transactionCredit = new Transaction();
        transactionCredit.setMontant(montant);
        transactionCredit.setType("reception");
        transactionCredit.setDescription("Réception de l'utilisateur ID : " + source.getUserId());
        transactionCredit.setDate(new Date());
        transactionCredit.setStatut("effectuée");
        transactionCredit.setCompte(destination);
        transactionCredit.setSourceUserId(source.getUserId());
        transactionCredit.setDestinationUserId(destination.getUserId());
        transactionRepo.save(transactionCredit);

        return List.of(transactionDebit, transactionCredit);
    }

//    public List<Transaction> listerTransactions(Long compteId) {
//        return transactionRepo.findByCompteId(compteId);
//    }


// Liste des transactions pour un utilisateur spécifique
//public List<Transaction> listerTransactionsParUserId(Long userId) {
//
//        return transactionRepo.findByCompteUserId(userId);
//}

    public List<Transaction> listerTransactionsParUserId(Long userId) {
        List<Transaction> transactions = transactionRepo.findByCompteUserId(userId);
        // Trier les transactions par date de manière décroissante (les plus récentes en premier)
        transactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        transactions.forEach(transaction -> {
            if ("transfert".equalsIgnoreCase(transaction.getType())) {
                Long destinationUserId = transaction.getDestinationUserId();
                Long sourceUserId = transaction.getSourceUserId();

                try {
                    Map<String, Object> sourceUser = userServiceClient.getClientById(sourceUserId);
                    Map<String, Object> destinationUser = userServiceClient.getClientById(destinationUserId);

                    String sourceUserName = sourceUser != null ? sourceUser.get("nom") + " " + sourceUser.get("prenom") : "Inconnu";
                    String destinationUserName = destinationUser != null ? destinationUser.get("nom") + " " + destinationUser.get("prenom") : "Inconnu";

                    transaction.setDescription("Transfert de " + sourceUserName + " à " + destinationUserName);
                } catch (Exception e) {
                    System.err.println("Erreur lors de la récupération des noms des utilisateurs : " + e.getMessage());
                }
            } else if ("reception".equalsIgnoreCase(transaction.getType())) {
                Long sourceUserId = transaction.getSourceUserId();
                try {
                    Map<String, Object> sourceUser = userServiceClient.getClientById(sourceUserId);

                    String sourceUserName = sourceUser != null ? sourceUser.get("nom") + " " + sourceUser.get("prenom") : "Inconnu";
                    transaction.setDescription("Réception de " + sourceUserName);
                } catch (Exception e) {
                    System.err.println("Erreur lors de la récupération du nom de l'utilisateur source : " + e.getMessage());
                }
            }
        });

        return transactions;
    }




    public BigDecimal consulterSolde(Long compteId) {
        Compte compte = compteRepo.findById(compteId).orElseThrow(() -> new RuntimeException("Compte non trouvé"));
        return compte.getSolde();
    }

    public void verifierClient(Long clientId) {
        Map<String, Object> client = userServiceClient.getClientById(clientId);
        if (client == null || client.isEmpty()) {
            throw new RuntimeException("Client non trouvé pour l'ID : " + clientId);
        }
        System.out.println("Client trouvé : " + client.get("nom") + " " + client.get("prenom"));
    }
    public BigDecimal calculerSoldeParUserId(Long userId) {
        // Récupérer le compte lié à cet utilisateur
        Compte compte = compteRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé pour l'utilisateur ID : " + userId));

        // Retourner directement le solde du compte
        return compte.getSolde();
    }


    public BigDecimal ajouterMontantAuSolde(Long userId, BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Le montant à ajouter doit être positif.");
        }

        Compte compte = compteRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé pour l'utilisateur ID : " + userId));

        compte.setSolde(compte.getSolde().add(montant));
        compteRepo.save(compte);

        return compte.getSolde();
    }


    public void transfererParTelephone(Long userIdSource, String telephoneDestination, BigDecimal montant) {
        // Vérifier que le montant est valide
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Le montant doit être supérieur à 0.");
        }

        // Récupérer le compte source
        Compte compteSource = compteRepo.findByUserId(userIdSource)
                .orElseThrow(() -> new RuntimeException("Compte source non trouvé pour l'utilisateur ID : " + userIdSource));

        // Vérifier que le solde est suffisant
        if (compteSource.getSolde().compareTo(montant) < 0) {
            throw new RuntimeException("Solde insuffisant. Solde actuel : " + compteSource.getSolde());
        }

        // Récupérer le compte destination par numéro de téléphone
        Compte compteDestination = compteRepo.findByTelephone(telephoneDestination)
                .orElseThrow(() -> new RuntimeException("Aucun compte trouvé avec ce numéro de téléphone : " + telephoneDestination));

        // Débiter le compte source
        compteSource.setSolde(compteSource.getSolde().subtract(montant));
        compteRepo.save(compteSource);

        // Créditer le compte destination
        compteDestination.setSolde(compteDestination.getSolde().add(montant));
        compteRepo.save(compteDestination);

        // Créer une transaction pour le débit
        Transaction transactionDebit = new Transaction();
        transactionDebit.setMontant(montant.negate());
        transactionDebit.setType("transfert");
        transactionDebit.setDescription("Transfert à " + compteDestination.getUserId());
        transactionDebit.setDate(new Date());
        transactionDebit.setStatut("effectuée");
        transactionDebit.setCompte(compteSource);
        transactionDebit.setSourceUserId(userIdSource);
        transactionDebit.setDestinationUserId(compteDestination.getUserId());
        transactionRepo.save(transactionDebit);

        // Créer une transaction pour le crédit
        Transaction transactionCredit = new Transaction();
        transactionCredit.setMontant(montant);
        transactionCredit.setType("reception");
        transactionCredit.setDescription("Réception de " + compteSource.getUserId());
        transactionCredit.setDate(new Date());
        transactionCredit.setStatut("effectuée");
        transactionCredit.setCompte(compteDestination);
        transactionCredit.setSourceUserId(userIdSource);
        transactionCredit.setDestinationUserId(compteDestination.getUserId());
        transactionRepo.save(transactionCredit);
    }


}
