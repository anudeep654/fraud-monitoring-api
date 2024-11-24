package com.fraudguard.notification.service;

import com.fraudguard.shared.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmailService emailService;
    
    @KafkaListener(topics = "fraud-events", groupId = "notification-service")
    public void handleFraudEvent(Transaction transaction) {
        log.info("Received fraud event for transaction: {}", transaction.getTransactionId());
        
        String subject = String.format("Fraud Alert - Transaction %s", transaction.getTransactionId());
        String message = buildFraudAlertMessage(transaction);
        
        // In a real system, you would look up the customer's email from a user service
        String customerEmail = "customer@example.com";
        emailService.sendFraudAlert(customerEmail, subject, message);
    }
    
    private String buildFraudAlertMessage(Transaction transaction) {
        return String.format("""
            Potential fraud detected for transaction:
            
            Transaction ID: %s
            Amount: %s %s
            Risk Score: %s
            Status: %s
            Reason: %s
            
            Please review this transaction immediately and contact our fraud department if you did not authorize it.
            """,
            transaction.getTransactionId(),
            transaction.getAmount(),
            transaction.getCurrency(),
            transaction.getRiskScore(),
            transaction.getStatus(),
            transaction.getReason()
        );
    }
}
