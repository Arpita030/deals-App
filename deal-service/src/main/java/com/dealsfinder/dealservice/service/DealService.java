package com.dealsfinder.dealservice.service;

import com.dealsfinder.dealservice.model.Deal;
import com.dealsfinder.dealservice.repository.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DealService {

    @Autowired
    private DealRepository dealRepository;

    public List<Deal> getAllDeals() {
        return dealRepository.findAll();
    }

    public Optional<Deal> getDealById(Long id) {
        return dealRepository.findById(id);
    }

    public List<Deal> getDealsByCategory(String category) {
        return dealRepository.findByCategory(category);
    }

    public List<Deal> getActiveDeals() {
        return dealRepository.findByIsActive(true);
    }

    public Deal saveDeal(Deal deal) {
        return dealRepository.save(deal);
    }

    public Deal updateDeal(Long id, Deal dealDetails) {
        return dealRepository.findById(id)
                .map(existingDeal -> {
                    existingDeal.setTitle(dealDetails.getTitle());
                    existingDeal.setDescription(dealDetails.getDescription());
                    existingDeal.setDiscount(dealDetails.getDiscount());
                    existingDeal.setCategory(dealDetails.getCategory());
                    existingDeal.setExpiryDate(dealDetails.getExpiryDate());
                    existingDeal.setActive(dealDetails.isActive());
                    return dealRepository.save(existingDeal);
                })
                .orElseThrow(() -> new RuntimeException("Deal not found with id: " + id));
    }

    public void deleteDeal(Long id) {
        dealRepository.deleteById(id);
    }
}
