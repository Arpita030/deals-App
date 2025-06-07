package com.dealsfinder.cashbackservice.controller;

import com.dealsfinder.cashbackservice.dto.CashbackDTO;
import com.dealsfinder.cashbackservice.entity.Cashback;
import com.dealsfinder.cashbackservice.entity.CashbackSummary;
import com.dealsfinder.cashbackservice.repository.CashbackRepository;
import com.dealsfinder.cashbackservice.repository.CashbackSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cashback")
@RequiredArgsConstructor
public class CashbackController {

    private final CashbackRepository cashbackRepository;
    private final CashbackSummaryRepository cashbackSummaryRepository;

    @PostMapping("/add")
    public Cashback addCashback(@RequestBody Cashback cashback) {
        cashback.setTimestamp(LocalDateTime.now());
        Cashback saved = cashbackRepository.save(cashback);
        CashbackSummary summary = cashbackSummaryRepository
                .findById(cashback.getUserEmail())
                .orElse(new CashbackSummary(cashback.getUserEmail(), 0.0));

        summary.setTotalCashback(summary.getTotalCashback() + cashback.getCashbackAmount());
        cashbackSummaryRepository.save(summary);

        return saved;
    }



    @GetMapping("/user/{email}")
    public List<CashbackDTO> getCashbackForUser(@PathVariable String email) {
        return cashbackRepository.findByUserEmail(email)
                .stream()
                .map(cb -> new CashbackDTO(cb.getDealId(), cb.getCashbackAmount(),cb.getTimestamp()))
                .toList();
    }

    @GetMapping("/summary/{email}")
    public double getTotalCashbackSummary(@PathVariable String email) {
        return cashbackSummaryRepository
                .findById(email)
                .map(CashbackSummary::getTotalCashback)
                .orElse(0.0);
    }

    @GetMapping("/user/{email}/total")
    public double getTotalCashbackFromTransactions(@PathVariable String email) {
        return cashbackRepository.findByUserEmail(email)
                .stream()
                .mapToDouble(Cashback::getCashbackAmount)
                .sum();
    }
}
