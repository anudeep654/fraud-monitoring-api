package com.fraudguard.fraud.controller;

import com.fraudguard.shared.model.Transaction;
import com.fraudguard.fraud.service.FraudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
public class FraudController {
    private final FraudService fraudService;

    @PostMapping("/validate")
    public ResponseEntity<Transaction> validateTransaction(@Valid @RequestBody Transaction transaction) {
        Transaction result = fraudService.validateTransaction(transaction);
        return ResponseEntity.ok(result);
    }
}
