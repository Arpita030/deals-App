package com.dealsfinder.dealservice.repository;

import com.dealsfinder.dealservice.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    List<Deal> findByCategory(String category);
    List<Deal> findByIsActive(boolean isActive);
}
