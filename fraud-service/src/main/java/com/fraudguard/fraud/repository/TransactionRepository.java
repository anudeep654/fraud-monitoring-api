package com.fraudguard.fraud.repository;

import com.fraudguard.shared.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
