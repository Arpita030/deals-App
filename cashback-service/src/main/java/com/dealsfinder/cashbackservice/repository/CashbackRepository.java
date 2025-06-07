package com.dealsfinder.cashbackservice.repository;

import com.dealsfinder.cashbackservice.entity.Cashback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CashbackRepository extends JpaRepository<Cashback, Long> {
    List<Cashback> findByUserEmail(String userEmail);
}
