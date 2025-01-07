package org.example.servicepaymenttransaction.Services;

import org.example.servicepaymenttransaction.Models.Compte;
import org.example.servicepaymenttransaction.Models.Transaction;
import org.example.servicepaymenttransaction.Repositories.CompteRepository;
import org.example.servicepaymenttransaction.Repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CompteRepository compteRepository;

    public Transaction saveTransaction(Transaction transaction) {

        return transactionRepository.save(transaction);
    }
}
