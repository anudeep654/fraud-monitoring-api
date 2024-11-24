package com.fraudguard.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    @NotNull
    private String transactionId;
    
    @NotNull
    @Positive
    private BigDecimal amount;
    
    @NotNull
    private String currency;
    
    @NotNull
    private String customerId;
    
    private String merchantId;
    private String ipAddress;
    private String deviceId;
    private String location;
    private Long timestamp;
}
