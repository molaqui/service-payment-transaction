package org.example.servicepaymenttransaction.Models;

import jakarta.persistence.*;
import lombok.Data;
import org.example.servicepaymenttransaction.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Date;


@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal montant;
    private String type; // "paiement", "reception", "transfert"
    private String description;
    private Date date;
    private String statut; // "effectuée", "échouée"

    private Long sourceUserId;       // ID de l'expéditeur
    private Long destinationUserId;  // ID du destinataire

    @Enumerated(EnumType.STRING) // Store the enum as a string in the database
    private TransactionType transactionType;

    @ManyToOne
    private Compte compte;
    @Transient
    private Long operatorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Long getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(Long sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public Long getDestinationUserId() {
        return destinationUserId;
    }

    public void setDestinationUserId(Long destinationUserId) {
        this.destinationUserId = destinationUserId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Compte getCompte() {
        return compte;
    }

    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
}
