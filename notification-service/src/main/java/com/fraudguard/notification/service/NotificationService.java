package com.fraudguard.notification.service;

import com.fraudguard.shared.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final JavaMailSender emailSender;

    @KafkaListener(topics = "fraud-events", groupId = "notification-service")
    public void handleFraudEvent(Transaction transaction) {
        log.info("Received fraud event for transaction: {}", transaction.getTransactionId());
        
        // Send email notification
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("fraud-alerts@fraudguard.com");
        message.setTo("security@fraudguard.com");
        message.setSubject("High Risk Transaction Alert");
        
        String emailBody = String.format(
            "High risk transaction detected:\n\n" +
            "Transaction ID: %s\n" +
            "Amount: %s %s\n" +
            "Customer ID: %s\n" +
            "Merchant ID: %s\n" +
            "Risk Score: %s\n" +
            "IP Address: %s\n" +
            "Device ID: %s\n" +
            "Location: %s\n" +
            "Timestamp: %s\n",
            transaction.getTransactionId(),
            transaction.getAmount(),
            transaction.getCurrency(),
            transaction.getCustomerId(),
            transaction.getMerchantId(),
            transaction.getRiskScore(),
            transaction.getIpAddress(),
            transaction.getDeviceId(),
            transaction.getLocation(),
            transaction.getTimestamp()
        );
        
        message.setText(emailBody);
        emailSender.send(message);
        
        log.info("Sent email notification for transaction: {}", transaction.getTransactionId());
    }
}
