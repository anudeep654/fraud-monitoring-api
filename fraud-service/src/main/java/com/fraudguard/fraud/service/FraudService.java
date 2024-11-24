package com.fraudguard.fraud.service;

import com.fraudguard.shared.model.Transaction;
import com.fraudguard.fraud.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudService {
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private final ModelServiceClient modelServiceClient;

    public Transaction validateTransaction(Transaction transaction) {
        // Set transaction ID and timestamp if not present
        if (transaction.getTransactionId() == null) {
            transaction.setTransactionId(UUID.randomUUID().toString());
        }
        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(Instant.now());
        }

        // Get risk prediction from model service
        Transaction enrichedTransaction = modelServiceClient.predictRisk(transaction);

        // Save transaction to database
        Transaction savedTransaction = transactionRepository.save(enrichedTransaction);

        // If risk is high, publish to Kafka for notification
        if (savedTransaction.getRiskScore().doubleValue() > 0.7) {
            kafkaTemplate.send("fraud-events", savedTransaction);
            log.info("Published high-risk transaction to Kafka: {}", savedTransaction.getTransactionId());
        }

        return savedTransaction;
    }
}
