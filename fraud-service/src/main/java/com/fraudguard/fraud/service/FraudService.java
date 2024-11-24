package com.fraudguard.fraud.service;

import com.fraudguard.dto.TransactionRequest;
import com.fraudguard.dto.TransactionResponse;
import com.fraudguard.shared.model.Transaction;
import com.fraudguard.fraud.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudService {
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private final ModelServiceClient modelServiceClient;

    public TransactionResponse validateTransaction(TransactionRequest request) {
        // Call model service for risk scoring
        double riskScore = modelServiceClient.predictRisk(request);
        
        // Apply business rules
        TransactionResponse.FraudStatus status = evaluateRiskScore(riskScore);
        String reason = determineReason(status, riskScore);
        
        // Create transaction record
        Transaction transaction = Transaction.builder()
            .transactionId(request.getTransactionId())
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .customerId(request.getCustomerId())
            .merchantId(request.getMerchantId())
            .ipAddress(request.getIpAddress())
            .deviceId(request.getDeviceId())
            .location(request.getLocation())
            .status(mapStatus(status))
            .riskScore(java.math.BigDecimal.valueOf(riskScore))
            .reason(reason)
            .timestamp(Instant.now())
            .build();
        
        // Save transaction
        transactionRepository.save(transaction);
        
        // Publish event if fraud detected
        if (status != TransactionResponse.FraudStatus.APPROVED) {
            kafkaTemplate.send("fraud-events", transaction);
        }
        
        // Build response
        return TransactionResponse.builder()
            .transactionId(transaction.getTransactionId())
            .status(status)
            .riskScore(transaction.getRiskScore())
            .reason(transaction.getReason())
            .recommendedAction(getRecommendedAction(status))
            .timestamp(transaction.getTimestamp())
            .build();
    }
    
    private TransactionResponse.FraudStatus evaluateRiskScore(double riskScore) {
        if (riskScore < 0.3) {
            return TransactionResponse.FraudStatus.APPROVED;
        } else if (riskScore < 0.7) {
            return TransactionResponse.FraudStatus.REVIEW_REQUIRED;
        } else {
            return TransactionResponse.FraudStatus.REJECTED;
        }
    }
    
    private String determineReason(TransactionResponse.FraudStatus status, double riskScore) {
        switch (status) {
            case APPROVED:
                return "Transaction appears legitimate";
            case REVIEW_REQUIRED:
                return "Transaction requires manual review due to medium risk score";
            case REJECTED:
                return "High risk transaction detected";
            default:
                return "Unknown status";
        }
    }
    
    private String getRecommendedAction(TransactionResponse.FraudStatus status) {
        switch (status) {
            case APPROVED:
                return "Process transaction";
            case REVIEW_REQUIRED:
                return "Hold for manual review";
            case REJECTED:
                return "Reject transaction and flag account";
            default:
                return "Unknown action";
        }
    }
    
    private Transaction.TransactionStatus mapStatus(TransactionResponse.FraudStatus status) {
        return Transaction.TransactionStatus.valueOf(status.name());
    }
}
