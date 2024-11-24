package com.fraudguard.model.service;

import com.fraudguard.dto.TransactionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Slf4j
@Service
public class RiskScoringService {
    private final Random random = new Random();

    @Cacheable(value = "riskScores", key = "#request.transactionId")
    public double predictRisk(TransactionRequest request) {
        // This is a placeholder implementation
        // In a real system, you would:
        // 1. Load your ML model
        // 2. Preprocess the transaction data
        // 3. Make predictions using the model
        // 4. Post-process the results
        
        log.info("Calculating risk score for transaction: {}", request.getTransactionId());
        
        // Simulate model prediction with basic rules
        double baseScore = random.nextDouble(); // Base random score
        
        // Add risk factors based on amount
        if (request.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0) {
            baseScore += 0.2;
        }
        
        // Add risk factors based on location
        if (request.getLocation() != null && 
            request.getLocation().equalsIgnoreCase("high_risk_country")) {
            baseScore += 0.3;
        }
        
        // Normalize score between 0 and 1
        return Math.min(1.0, baseScore);
    }
}
