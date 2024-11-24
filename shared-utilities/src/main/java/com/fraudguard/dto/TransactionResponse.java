package com.fraudguard.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class TransactionResponse {
    private String transactionId;
    private FraudStatus status;
    private BigDecimal riskScore;
    private String reason;
    private String recommendedAction;
    private Instant timestamp;
    
    public enum FraudStatus {
        APPROVED,
        REJECTED,
        REVIEW_REQUIRED
    }
}
