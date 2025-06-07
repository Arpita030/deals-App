package com.dealsfinder.paymentservice.repository;

import com.dealsfinder.paymentservice.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
    List<PaymentTransaction> findByUserEmail(String userEmail);
}
