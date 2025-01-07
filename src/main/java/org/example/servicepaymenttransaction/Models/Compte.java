package org.example.servicepaymenttransaction.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // ID de l'utilisateur associé au compte
    private BigDecimal solde;

    @Column(unique = true)
    private String telephone; // Numéro de téléphone associé au compte

    @Enumerated(EnumType.STRING) // Stocker les valeurs de l'énumération sous forme de chaîne
    private Hssab hssab;

    @ElementCollection // Used to persist a list of simple types (like Strings) in a separate table
    @CollectionTable(name = "virtual_cards", joinColumns = @JoinColumn(name = "compte_id"))
    @Column(name = "virtual_card")
    private List<String> virtual_cards;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getSolde() { return solde; }
    public void setSolde(BigDecimal solde) { this.solde = solde; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public Hssab getHssab() { return hssab; }
    public void setHssab(Hssab hssab) { this.hssab = hssab; }

    public List<String> getVirtual_cards() { return virtual_cards; }
    public void setVirtual_cards(List<String> virtual_cards) { this.virtual_cards = virtual_cards; }
}
