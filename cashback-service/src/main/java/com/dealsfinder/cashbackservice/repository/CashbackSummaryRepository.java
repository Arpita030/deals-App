package com.dealsfinder.cashbackservice.repository;

import com.dealsfinder.cashbackservice.entity.CashbackSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashbackSummaryRepository extends JpaRepository<CashbackSummary, String> {
}
