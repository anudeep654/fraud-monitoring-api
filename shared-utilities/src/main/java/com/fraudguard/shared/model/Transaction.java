package com.fraudguard.shared.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    private String transactionId;
    
    private BigDecimal amount;
    private String currency;
    private String customerId;
    private String merchantId;
    private String ipAddress;
    private String deviceId;
    private String location;
    
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    
    private BigDecimal riskScore;
    private String reason;
    private Instant timestamp;
    
    public enum TransactionStatus {
        APPROVED,
        REJECTED,
        REVIEW_REQUIRED
    }
}
