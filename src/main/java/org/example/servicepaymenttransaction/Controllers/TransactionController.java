package org.example.servicepaymenttransaction.Controllers;

import org.example.servicepaymenttransaction.Models.Transaction;
import org.example.servicepaymenttransaction.Services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Endpoint to save a transaction
    @PostMapping("/save")
    public ResponseEntity<Transaction> saveTransaction(@RequestBody Transaction transaction) {
        try {
            Transaction savedTransaction = transactionService.saveTransaction(transaction);
            return ResponseEntity.ok(savedTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
