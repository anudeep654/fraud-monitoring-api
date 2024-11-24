package com.fraudguard.model.controller;

import com.fraudguard.dto.TransactionRequest;
import com.fraudguard.model.service.RiskScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/model")
@RequiredArgsConstructor
public class ModelController {
    private final RiskScoringService riskScoringService;
    
    @PostMapping("/predict")
    public ResponseEntity<Double> predictRisk(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(riskScoringService.predictRisk(request));
    }
}
