package com.dealsfinder.dealservice.controller;

import com.dealsfinder.dealservice.model.Deal;
import com.dealsfinder.dealservice.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/deals")
public class DealController {

    @Autowired
    private DealService dealService;

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Deal> getAllDealsForAdmin() {
        return dealService.getAllDeals();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Deal> getAllDeals() {
        return dealService.getAllDeals();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Optional<Deal> getDealById(@PathVariable Long id) {
        return dealService.getDealById(id);
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Deal> getDealsByCategory(@PathVariable String category) {
        return dealService.getDealsByCategory(category);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Deal> getActiveDeals() {
        return dealService.getActiveDeals();
    }

    // ADMIN-only access
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Deal createDeal(@RequestBody Deal deal) {
        return dealService.saveDeal(deal);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Deal updateDeal(@PathVariable Long id, @RequestBody Deal dealDetails) {
        return dealService.updateDeal(id, dealDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDeal(@PathVariable Long id) {
        dealService.deleteDeal(id);
    }
}
